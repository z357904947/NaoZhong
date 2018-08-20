package com.example.z3579.naozhong;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.z3579.naozhong.entity.Clock;
import com.example.z3579.naozhong.entity.Ring;
import com.example.z3579.naozhong.until.ClockDAO;
import com.example.z3579.naozhong.until.LableFragment;
import com.example.z3579.naozhong.until.PlayRing;
import com.example.z3579.naozhong.until.RingRecyclerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Clock_listFragment.OnListFragmentInteractionListener,AddFragment.OnFragmentInteractionListener
        ,setchongfu_Fragment.OnFragmentInteractionListener,LableFragment.OnFragmentInteractionListener ,RingFragment.OnFragmentInteractionListener, RingRecyclerViewAdapter.OnListener{
    private FloatingActionButton fab;//浮动按钮

    //定义年月日星期显示控件
    private TextView yyyyMMddEEEE_text;
    //定义当系统为12小时制时上下午显示控件
    private TextView text_12;
    //定义上下午标记,日标记，因为上下午，年月日显示控件变化极少，为减少每分钟对这类控件操作，定义标记进行判断
    //-1,默认值，代表还未验证更新上下午，0代表上午，1代表下午
    private int ap_am=-1;
    //0，默认值，代表还未验证更新
    private int day=0;
    //定义小时显示控件
    private ImageView hour_1;
    private ImageView hour_2;
    //定义分钟显示控件
    private ImageView minute_1;
    private ImageView minute_2;
    //时间数字资源集合
    Map<String,Integer> map_num=null;
    //application
    private MyApplication application;
    //闹钟数据集合
    private List<Clock> list;
    //闹钟显示所在fragment
    private Clock_listFragment    clock_listFragment=null;
    //LOG标记
    private final String LOG_TAG="MainActivity_测试";
    //Ring铃声对象
    public static Ring ring;
    //标记位，判断是否显示右上角菜单
    private boolean isadd=true;
    //广播接收器
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_TIME_TICK.equals(action)) {//监听系统时间分钟变化
                getNewTimeToShow();
            }
        }
    };
    public static Clock clock;
    /**
     * 得到当前时间并调用刷新控件方法
     */
    private void getNewTimeToShow(){
        Calendar calendar = Calendar.getInstance();//获取当前时间
            if(day!=calendar.get(Calendar.DAY_OF_MONTH)){//如果日期发生改变
                //用SimpleDateFormat处理日期格式得到我们需要的年月日和星期格式，调用更新年月日和星期方法
                String yyyyMMddEEEE = new SimpleDateFormat("yyyy年MM月dd日 EEEE").format(calendar.getTime());
                yyyyMMddEEEE_text.setText(yyyyMMddEEEE);
                day=calendar.get(Calendar.DAY_OF_MONTH);//更新标志位
        }
        int amorpm=calendar.get(Calendar.AM_PM);//得到上下午，0代表上午，1代表下午
        int minute=calendar.get(Calendar.MINUTE);//得到分钟
        int hour;
        if(application.isFalg12or24()){//12小时制
            //12小时制为0-11，0代表12点,
            if(calendar.get(Calendar.HOUR)==0){
                hour=12;
            }else {
                hour=calendar.get(Calendar.HOUR);
            }
        }else {//24小时制
            hour=calendar.get(Calendar.HOUR_OF_DAY);
        }
        showNewTime(hour,minute,amorpm);
    }
    /**
     * 在onPause中销毁广播
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG,"销毁广播");
        //在于onResume对应的onPause方法中进行广播销毁，动态注册广播必须销毁。
        unregisterReceiver(receiver);
    }
    /**
     * 在onResume方法中执行广播动态注册
     */
    @Override
    protected void onResume() {
        super.onResume();
        //在onResume中查询当前时间并显示，保证Activity每次可见时时间都是最新的。
        //判断系统12or24小时制，放在onResume方法中进行判断是因为用户可能在Activity不可见时修改了系统时间制式。
        // 放在onResume里进行判断可以及时响应用户对系统设置的修改
        boolean f =application.isFalg12or24();
        true12or24();
        if(f!=application.isFalg12or24()){//判断用户是否在activity不可见时修改了时间制式
            //刷新列表,调用Clock_listFragment的显示方法直接进行替换。
            clock_listFragment.showClockList();
        }
        //得到当前时间，并调用显示方法。
        getNewTimeToShow();
        //更新时间后注册广播
        timeService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        //隐藏toolbar自带标题，因为toolbar自带标题无法实现居中。
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //隐藏右上角菜单

        /**
         * 设置NavigationIcon图标点击监听方法有两种
         * 1.setSupportActionBar(toolbar);
         * toolbar.setNavigationOnClickListener(this);
         * 方法1需要注意的是必须放在setSupportActionBar方法之后。
         *2.在onOptionsItemSelected方法中判断
         * if(id==android.R.id.home){
         //Toast.makeText(MainActivity.this, "You Clicked Lock Icon", Toast.LENGTH_SHORT).show();
         }
         */
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        init();//初始化
        //代码模板生成的浮动按钮

        fab =  findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            //浮动按钮实现打开新增闹钟fragment
             //clock对象清空
            clock=null;
            ring=null;
            setIsadd(false);
            ring= PlayRing.getPlayRing(MainActivity.this).getDefaultUrl();
            AddFragment fragment  =  AddFragment.newInstance(clock);//新增，参数Null及代表无闹钟数据，因为修改页面和新增页面一样。
            getSupportFragmentManager().beginTransaction().replace(R.id.listfragment,fragment,"add_fragment").addToBackStack("add_fragment").commit();
            fab.hide();
            }
        });
    }
    public void setIsadd(boolean isadd){
        this.isadd =isadd;
        invalidateOptionsMenu();//调用系统方法，重新绘制菜单
    }
    /**
     * 判断当前系统使用24小时制还是12小时制，并进行上下午显示控件的显示和隐藏
     */
    private void true12or24(){
        if(DateFormat.is24HourFormat(this)){
            //在布局XML文件中默认上午下午不可见
            application.setFalg12or24(false);
            Log.d(LOG_TAG,"当前24小时制");
            if(text_12.getVisibility()==View.VISIBLE){
                text_12.setVisibility(View.GONE);
                ap_am=-1;
            }

        }else {
            Log.d(LOG_TAG,"当前12小时制");
            application.setFalg12or24(true);//修改标记
            //如果控件当前不可见修改状态为可见
            if(text_12.getVisibility()!=View.VISIBLE){
                text_12.setVisibility(View.VISIBLE);
            }
        }
    }
    //初始化
    private void init(){
        //拿到application
        application =(MyApplication) getApplication();
        //时间显示控件初始化
        yyyyMMddEEEE_text=findViewById(R.id.time_yyyymmdd);
        text_12=findViewById(R.id.time_12);
        hour_1=findViewById(R.id.time_hour1);
        hour_2=findViewById(R.id.time_hour2);
        minute_1=findViewById(R.id.time_minute1);
        minute_2=findViewById(R.id.time_minute2);
        //冒号ImageView
        ImageView  colon =findViewById(R.id.time_colon);
        colonChange(colon);//冒号闪烁动画
        //将资源文件加入集合
        map_num=new HashMap<>();
        map_num.put("0",R.mipmap.num_0);
        map_num.put("1",R.mipmap.num_1);
        map_num.put("2",R.mipmap.num_2);
        map_num.put("3",R.mipmap.num_3);
        map_num.put("4",R.mipmap.num_4);
        map_num.put("5",R.mipmap.num_5);
        map_num.put("6",R.mipmap.num_6);
        map_num.put("7",R.mipmap.num_7);
        map_num.put("8",R.mipmap.num_8);
        map_num.put("9",R.mipmap.num_9);
        //先在onCreate执行一遍系统时间制式确认
        true12or24();
        //显示listFragment
        clock_listFragment = Clock_listFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.listfragment,clock_listFragment).commit();
//        //显示列表
//      showClockList();
    }

    /**
     * 刷新控件显示上下午小时分钟
     */
    private void showNewTime(int hour,int minute,int amorpm){
        //得到小时的个位和十位数字
        Log.d("测试","小时"+hour);
        int hour_shi =hour/10;//十位
        int hour_ge = hour-(hour/10)*10;//个位
        //分钟的十位和个位
        int minute_shi=minute/10;
        int minute_ge=minute-(minute/10)*10;
        //根据时间显示map中对应的资源文件。
        if(map_num!=null){
            if(application.isFalg12or24()){//12小时制
                if(ap_am!=amorpm){//如果不等,执行更新,降低对显示上下午控件操作频率
                    //显示上下午控件
                    if(amorpm==0){
                        text_12.setText(R.string.ap);
                    }else {
                        text_12.setText(R.string.am);
                    }
                    ap_am=amorpm;//更新标志位
                }
            }
            if(hour_shi!=0){//小时的十位不为0时进行显示，否则不显示

                if( hour_1.getVisibility()!=View.VISIBLE){
                    hour_1.setVisibility(View.VISIBLE);
                }
                hour_1.setImageResource(map_num.get(hour_shi+""));
            }else{

                if( hour_1.getVisibility()==View.VISIBLE){
                    hour_1.setVisibility(View.GONE);
                }
            }
            hour_2.setImageResource(map_num.get(hour_ge+""));
            minute_1.setImageResource(map_num.get(minute_shi+""));
            minute_2.setImageResource(map_num.get(minute_ge+""));
        }
    }


    /**
     *动态注册时间变化广播接收器
     */
    private void timeService(){
        Log.d(LOG_TAG,"注册广播");
        IntentFilter filter=new IntentFilter();
        //表示只接收ACTION_TIME_TICK的广播
        filter.addAction(Intent.ACTION_TIME_TICK);//添加系统时间每分钟变化Action
        registerReceiver(receiver,filter);
    }
    /**
     * 实现时间中的冒号每秒闪烁，本次采用动画实现
     * 首先新建一个AlphaAnimation，透明度从完全可见到0.0f(完全不可见)。setDuration设置动画持续的时间未1000毫秒。LinearInterpolator表示动画以均匀的速率改变。
     * alphaAnimation.setRepeatCount(Animation.INFINITE); 表示重复多次。 也可以设定具体重复的次数，比alphaAnimation1.setRepeatCount(5);
     * alphaAnimation.setRepeatMode(Animation.REVERSE);表示动画结束后，反过来再执行。 该方法有两种值，
     *  RESTART 和 REVERSE。 RESTART表示从头开始，REVERSE表示从末尾倒播。这里用RESTART。
     */
    private void colonChange(View view){
        if( null == view ){
            return;
        }
        //让冒号在每秒内由100%可见到不可见进行循环
        Animation alphaAnimation = new AlphaAnimation( 1, 0.0f );
        alphaAnimation.setDuration( 1000 );
        alphaAnimation.setInterpolator( new LinearInterpolator( ) );
        alphaAnimation.setRepeatCount( Animation.INFINITE );
        alphaAnimation.setRepeatMode( Animation.RESTART );
        view.startAnimation( alphaAnimation );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(this.isadd){
            menu.findItem(R.id.action_settings).setVisible(false);
        }else {
            menu.findItem(R.id.action_settings).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings://右上角存储
                //将新的闹钟信息存入数据库
                if(clock!=null){
                    clock.setClock_ring(ring.getUrlStr());
                    //写入数据库
                    ClockDAO.getInstance(this).inserClock(clock);
                    //重新查询数据库得到最后一条数据，这里应该单独得到最后一条数据，
                    List<Clock> list=ClockDAO.getInstance(this).getClockList();
                    Clock c=ClockDAO.getInstance(this).getAddClock();
                    clock_listFragment.addClock(c);
                    onBackPressed();
                }
                return true;
            case android.R.id.home://Toolbar 左侧icn的ID
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(Integer item) {

    }
    //返回事件
    @Override
    public void onBackPressed() {
        //判断栈顶的fragment是哪一个，调用对应的方法
        FragmentManager fragmentManager= getSupportFragmentManager();
        //得到回退栈的fragment个数
        int count =fragmentManager.getBackStackEntryCount();
        Log.d("测试","count:"+count);
        if(count==2){
            //显示存储
            setIsadd(false);
        }
        setIsadd(false);
        if(count>=1){
            //得到栈顶的fragment
            FragmentManager.BackStackEntry  backStackEntry = fragmentManager.getBackStackEntryAt(count-1);
            String  tag= backStackEntry.getName();//得到该frangment标记
            Log.d("测试","tag:"+tag);
            if(tag.equals("set_chongfu")){//是星期重复设置页面
                //获得选中的重复星期数据
                setchongfu_Fragment fragment =(setchongfu_Fragment)fragmentManager.findFragmentByTag("set_chongfu");
                Log.d("测试","当前是set_chongfu");
                fragment.checkData();//返回选中数据
            }else if(tag.equals("lableFragment")){
                LableFragment lableFragment =(LableFragment)fragmentManager.findFragmentByTag("lableFragment");
                clock.setClock_note(lableFragment.getNote());

            }
        }

        super.onBackPressed();//注释掉这行,back键不退出activity
    }
    //AddFragment回调函数，从activity打开fragment
    @Override
    public void onFragmentInteraction() {
            //打开重复设置界面
        if(clock==null){
            clock=new Clock();
        }
        setchongfu_Fragment fragment  =  setchongfu_Fragment.newInstance(clock.getRepeat());
        setIsadd(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.listfragment,fragment,"set_chongfu").addToBackStack("set_chongfu").commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    /**
     * 得到星期重复选中数据
     * @param check 选中状态数组
     */
    @Override
    public void onGetData(boolean[] check) {
        if(check!=null){
            if(clock==null){
                clock=new Clock();
            }
            clock.setRepeat(check);
        }


    }

    @Override
    public void onFragmentDestroy() {
        fab.show();
        setIsadd(true);
    }
    //回调函数，闹钟标签修改
    @Override
    public void onLableUpdate() {
        //打开修改fragment页面。
        LableFragment lableFragment = LableFragment.newInstance(clock.getClock_note());
        setIsadd(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.listfragment,lableFragment,"lableFragment").addToBackStack("lableFragment").commit();
    }
    //回调函数，打开铃声设置页面
    @Override
    public void openringlist() {
        RingFragment ringFragment = RingFragment.newInstance();
        setIsadd(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.listfragment,ringFragment,"ringFragment").addToBackStack("ringFragment").commit();
    }

    @Override
    public void onFragmentInteraction(String note) {

    }

    @Override
    public void getCheck(Ring ring) {
        MainActivity.ring = ring;
    }

    @Override
    public void onFragmentInteraction(Ring ring) {

    }
}
