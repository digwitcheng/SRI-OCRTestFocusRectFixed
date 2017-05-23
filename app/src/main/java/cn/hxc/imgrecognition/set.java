package cn.hxc.imgrecognition;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

import cn.hxc.imgrecognition.MainActivity.btnOnTouchListener;
import cn.hxc.imgrecognitionSRI_OCR.R;

public class set extends Activity {
	static int marg = 0;
	private PreferencesService service;
	private CheckedTextView checkBtn;
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		checkBtn = (CheckedTextView) findViewById(R.id.checkBtn);

		service = new PreferencesService(this);
		Map<String, String> params = service.getPreferences("margain");// new
																		// Integer(params.get("margain")
		if (params.get("margain") == null) {
			service.save("0", "margain");
		}

		preferences = getSharedPreferences("set",
				MODE_PRIVATE);
		
		if (!preferences.contains("numLen")) {
			Editor editor = preferences.edit();
			editor.putInt("numLen", 0);
			editor.commit();
		}
		
		int numlen=preferences.getInt("numLen", 0);
		if(numlen==0){
			checkBtn.setChecked(false);
		}else{
			checkBtn.setChecked(true);
		}
		
		
	}

	public void checkClick(View v) {
		// ����checkedTextViewΪѡ��״̬
		// checkBtn.setChecked(true);
		// ����checkedTextView��ҳ�߾࣬������/��/��/�Ҹ�20���أ�Ĭ��Ϊδѡ��״̬
		// checkBtn.setPadding(20, 20, 20, 20);
		// checkBtn.setCheckMarkDrawable(android.R.drawable.arrow_down_float);
		// ����checkedTextView��ת״̬����Ĭ�ϵ�δѡ�з�תΪѡ��״̬
		checkBtn.toggle();
		int numlen=preferences.getInt("numLen", 0);
			Editor editor = preferences.edit();
			editor.putInt("numLen", (numlen+1)%2);
			editor.commit();
		
		
	}

	public void margain(View v) {
		// params.get("margain");
		Map<String, String> params = service.getPreferences("margain");// new
																		// Integer(params.get("margain")
		if (params.get("margain") == null) {
			service.save("90", "margain");
		}

		imageProcess.noequl(params.get("margain"), 333333333);
		int a = new Integer(params.get("margain")) + 90;
		a = a % 360;
		service.save(a + "", "margain");
		Toast.makeText(this, "�ɹ���ת90��", 1).show();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	public void save(View v) {
		Intent intent = new Intent(this, saveSmg.class);
		startActivity(intent);

		// String smsContent=content.getText().toString().trim();
		// service.save(smsContent);
		// Toast.makeText(this, "����ɹ�", 1).show();
		// Intent intent = new Intent(set.this,MainActivity.class);
		// startActivity(intent);
	}
}
