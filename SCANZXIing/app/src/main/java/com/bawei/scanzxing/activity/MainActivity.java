package com.bawei.scanzxing.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bawei.scanzxing.R;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static int SCANNIN_GREQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //找控件
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //第一种方式
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // 没有权限，申请权限。

                } else {
                    // 有权限了，去放肆吧。
                }
//第二种方式
                if (Build.VERSION.SDK_INT >= 23) {
//6.0以上版本不能直接运行 否则是空白页面 无法扫描
                    /**第一种方式 手动打开权限
                     * 第二种动态添加权限
                     */

                    if (AndPermission.hasPermission(MainActivity.this, Manifest.permission.CAMERA)) {
                        //有权限 做操作
                        Toast.makeText(MainActivity.this, "有权限", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this, MipcaActivityCapture.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //用startActivityForResult
                        startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                    } else {
                        //没有权限 申请权限
                        AndPermission.with(MainActivity.this).requestCode(100).permission(Manifest.permission.CAMERA).send();
                        Toast.makeText(MainActivity.this, "没有权限", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //6.0以下版本直接运行 就可以扫描二维码
               /* Intent intent = new Intent(MainActivity.this, MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);*/
                }


            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 只需要调用这一句，其它的交给AndPermission吧，最后一个参数是PermissionListener。
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。
            if (requestCode == 100) {
                // TODO 相应代码。
                Intent intent = new Intent(MainActivity.this, MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //用startActivityForResult
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
            } else if (requestCode == 101) {
                // TODO 相应代码。
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, deniedPermissions)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog(MainActivity.this, requestCode).show();

                // 第二种：用自定义的提示语。
                // AndPermission.defaultSettingDialog(this, REQUEST_CODE_SETTING)
                // .setTitle("权限申请失败")
                // .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
                // .setPositiveButton("好，去设置")
                // .show();

                // 第三种：自定义dialog样式。
                // SettingService settingService =
                //    AndPermission.defineSettingDialog(this, REQUEST_CODE_SETTING);
                // 你的dialog点击了确定调用：
                // settingService.execute();
                // 你的dialog点击了取消调用：
                // settingService.cancel();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCANNIN_GREQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                Log.i("xxx", result.toString());

                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("url", result);
                startActivity(intent);

            }
        }
    }
}
