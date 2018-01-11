package cn.lger.encyclopedia;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;
import cn.lger.encyclopedia.ui.ChatFragment;
import cn.lger.encyclopedia.ui.impl.TranslateFragment;
import cn.lger.encyclopedia.ui.impl.WeatherFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null)
            this.getFragmentManager().beginTransaction()
                    .add(R.id.content_main, TranslateFragment.getInstance()).commit();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //确保不会重复按下相同的菜单
        if (item.isChecked()) {
            System.out.println(item.getTitle() + "已选择");
            return true;
        }

        int id = item.getItemId();
        if (id == R.id.nav_translate) {
            this.getFragmentManager().beginTransaction()
                    .replace(R.id.content_main, TranslateFragment.getInstance()).commit();
        } else if (id == R.id.nav_weather) {
            this.getFragmentManager().beginTransaction()
                    .replace(R.id.content_main, WeatherFragment.getInstance()).commit();
        } else if (id == R.id.nav_exit){
            this.finish();
            System.exit(0);
        } else if (id == R.id.nav_chat){
            this.getFragmentManager().beginTransaction()
                    .replace(R.id.content_main, new ChatFragment()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按键为返回，并且为按下的状态
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        System.out.println("主Activity销毁");
        TranslateFragment.getInstance().destroyAllResource();
        WeatherFragment.getInstance().destroyAllResource();
        super.onDestroy();
    }

}
