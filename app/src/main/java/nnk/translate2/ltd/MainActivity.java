package nnk.translate2.ltd;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import nnk.translate2.ltd.Utils.GoogleApi;
import nnk.translate2.ltd.Utils.Languega;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final MyHandle handle = new MyHandle();

    private static final String NET_ERROR = "com.alibaba.fastjson.JSONException: syntax error, expect [, actual error, pos 0, fieldName null";

    RadioGroup radioGroup;
    Button bt_start, bt_copy, bt_why;
    EditText source_text, et_diy_number, et_result;
    Spinner tar_lang;
    int frequency = 1;
    String result = "";
    String lang = "";

    SharedPreferences settings_sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();  //自定义初始化视图函数
    }

    /**
     * 创建选项菜单
     *
     * @param menu Menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 选项菜单点击事件
     *
     * @param item MenuItem
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 初始化视图
     */
    private void initView() {
        settings_sp = getSharedPreferences("settings", MODE_PRIVATE);


        radioGroup = findViewById(R.id.radioGroup);
        et_diy_number = findViewById(R.id.et_diy_number);
        bt_start = findViewById(R.id.bt_start);
        source_text = findViewById(R.id.source_text);
        et_result = findViewById(R.id.et_result);
        bt_copy = findViewById(R.id.bt_copy);
        bt_why = findViewById(R.id.bt_why);
        tar_lang = findViewById(R.id.tar_lang);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                switch (radioButton.getId()) {
                    case R.id.number10:
                        frequency = 10;
                        break;
                    case R.id.number20:
                        frequency = 20;
                        break;
                    default:
                        frequency = 1;
                        break;
                }
                if (checkedId == R.id.number) {
                    et_diy_number.setVisibility(View.VISIBLE);
                    frequency = 0;
                } else {
                    et_diy_number.setVisibility(View.GONE);
                }
            }
        });

        bt_start.setOnClickListener(this);
        bt_copy.setOnClickListener(this);
        bt_why.setOnClickListener(this);

        et_result.setText(result);

        tar_lang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] languages = getResources().getStringArray(R.array.language_value);
                lang = languages[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    /**
     * Button点击事件监听
     *
     * @param v Button
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_start:
                try {
                    readyTranslate(v.getContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_copy:
                copyResult();
            case R.id.bt_why:
                showWhyDialog();
                break;
        }
    }

    private void showWhyDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("关于IP封禁");
        dialog.setMessage("翻译服务提供商为防止滥用或DDOS，通常会将发送大量请求的IP封禁，请在合理范围内使用本App~\n" +
                "如何解决？\n" + "等＞︿＜\n如果不想等可以选择捐赠开发者，使其能换上更高档次的翻译API~\n" +
                "当然我也不能强求您捐赠，所以下个版本会添加API密钥的支持，您可以自行申请免费API在本APP上使用~");
        dialog.setPositiveButton("确定", null);
        dialog.setNeutralButton("捐赠", null);//TODO 捐赠跳转
        dialog.show();
    }


    private void readyTranslate(Context context) {
        String text = source_text.getText().toString().trim();
        String diy_number = et_diy_number.getText().toString().trim();

        if (TextUtils.isEmpty(diy_number) && frequency == 0) {
            Toast.makeText(context, "次数不可以为空哦~", Toast.LENGTH_SHORT).show();
        } else {
            if (frequency == 0) {
                translate(text, Integer.valueOf(diy_number));
            } else {
                translate(text, frequency);
            }
        }
    }


    /**
     * @param text       待翻译文本
     * @param frequency1 翻译次数
     */
    private void translate(String text, int frequency1) {//TODO 翻译方法
        int i = 10 - frequency1;
        if (frequency1 <= 0) {
            try {
                result = GoogleApi.getInstance().translateText(text, "auto", lang);
                Log.d("nnk33", "result: " + result);
                handle.sendEmptyMessage(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        new Thread() {
            @Override
            public void run() {
                String result0 = "", result1;
                try {
                    String random_lang1 = "", random_lang2 = "", random_lang3 = "";
                    int index1 = (int) (Math.random() * Languega.langList.length);
                    int index2 = (int) (Math.random() * Languega.langList.length);
                    int index3 = (int) (Math.random() * Languega.langList.length);
                    random_lang1 = Languega.langList[index1];
                    random_lang2 = Languega.langList[index2];
                    random_lang3 = Languega.langList[index3];

                    Log.d("nnk33", "Random_lang1： " + random_lang1 + "\n");
                    Log.d("nnk33", "Random_lang2： " + random_lang2 + "\n");
                    Log.d("nnk33", "Random_lang3： " + random_lang3 + "\n");


                    result0 = GoogleApi.getInstance().translateText(text, "auto", random_lang1);
                    result1 = GoogleApi.getInstance().translateText(result0, "auto", random_lang2);
                    result0 = GoogleApi.getInstance().translateText(result1, "auto", random_lang3);
                    result = result0;
                    translate(result0, frequency1 - 2);
                    handle.sendEmptyMessage(2);
                } catch (Exception e) {
                    Log.d("nnk3333", "出错啦: " + e.toString());
                    if (e.toString().equals(NET_ERROR)) {
                        handle.sendEmptyMessage(3);
                    }
                    e.printStackTrace();
                }
            }
        }.start();
    }


    /**
     * 复制结果
     */
    private void copyResult() {
        ClipboardManager cm;
        ClipData mClipData;
        //获取剪贴板管理器：
        cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        mClipData = ClipData.newPlainText("result", result);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }


    class MyHandle extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    bt_copy.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "完成！", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    et_result.setText(result);
                    break;
                case 3:
                    bt_why.setVisibility(View.VISIBLE);
                    et_result.setText("IP被封禁，请稍后再试~");
            }
        }
    }

}