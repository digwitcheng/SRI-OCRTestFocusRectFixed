package cn.hxc.imgrecognition;

import android.R.array;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.ImageWriter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.security.PublicKey;
import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import cn.hxc.imgrecognitionSRI_OCR.R;

public class processActivity extends Activity {

	
	private EditText tv_num;
	private ImageView iv_photo;
	private Matrix matrix = new Matrix();
	private ImageView iv_2;
	private int[] newpixels;
	private int[] pixels;
	private int width;
	private int height;
	private PreferencesService service;
	private String content;
	private Bitmap greyBitmap;
	private Bitmap screenBitmap;
//	private int sorta[];
//	private Bitmap greyBitmap1;
//	private Bitmap sourceBitmap;
//	private boolean isWriteRecogize;
	private int[] by;
//	private boolean isOk;
//
//	private TextView label;

	// ��������
	private float mScreenWidth;
	private float mScreenHeight;
//	private float old_x;
//	private float old_y;
//	private float new_x;
//	private float new_y;
//	private boolean isInRect;// �ж���ָ����ʱ����ھ��ο��λ��
//	private boolean isInTop;
//	private boolean isInBottom;
//	private int newbottom;
//	private Canvas canvas;
//	private Paint paint;
//	private Rect rect;
//	private Bitmap paintbitMap;
//	private int offset;

	private SharedPreferences preferences;

	static {
		System.loadLibrary("processActivity");
	}

	public native String callint(int[] by1, int w, int h, String num,
			String win2, String whi2, String model, int flag);
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.process);
		iv_photo = (ImageView) findViewById(R.id.imgView1);
		iv_2 = (ImageView) findViewById(R.id.imgView2);
		tv_num = (EditText) findViewById(R.id.tv_num);
		//label = (TextView) findViewById(R.id.label);
		tv_num.setVisibility(View.GONE);
		WindowManager wm = (WindowManager) this
				.getSystemService(this.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth = outMetrics.widthPixels;
		mScreenHeight = outMetrics.heightPixels;
		//isWriteRecogize = false;// takePhoto.isWriteRecogize;
		//isOk = false;
		
		
		// dip��value��= (int) (px��value��/1.5 + 0.5)
		// imageProcess.noequl("newtop ", newtop);

		
		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		// ״̬���߶�
		int statusBarHeight = frame.top;
		View v = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
		int contentTop = v.getTop();
		// statusBarHeight�����������״̬���ĸ߶�
		int titleBarHeight = contentTop - statusBarHeight;
		imageProcess.noequl("statusBarHeight=", statusBarHeight);
		imageProcess.noequl("titleBarHeight=", titleBarHeight);

		preferences = getSharedPreferences("set", MODE_PRIVATE);
		content = preferences.getString("content", "��Ԥ�������������ö�������");
		// service = new PreferencesService(this);
		// Map<String, String> params = service.getPreferences("content");
		// content = params.get("content");
		// content.setSelection(content.getText().length());
		// ��ȡ���ҶȻ�
		try {
			greyScreen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// try {
		// // do {
		// // if (isWriteRecogize) {
		// // // ��ֵ��
		// // binaryzation(1);
		// // // ȥ�����ش�ĺͷǳ�С������
		// // removeMaxMinNoise(1);
		// // recoginze_h();
		// // } else {
		//
		// // ʶ��ӡˢ��
		// // ��ֵ��
		// binaryzation(0);
		// // ȥ�����ش�ĺͷǳ�С������
		// removeMaxMinNoise(0);
		// recoginze();
		// // imageProcess.noequl("isWriteRecogize is true", 1);
		// // }
		// // } while (isOk);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}
	public BitmapFactory.Options getOption(BitmapFactory.Options opts){
		//2.Ϊλͼ����100K�Ļ���
		//BitmapFactory.Options opts=new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
//4.����ͼƬ���Ա����գ�����Bitmap���ڴ洢Pixel���ڴ�ռ���ϵͳ�ڴ治��ʱ���Ա�����
		opts.inPurgeable = true;
//5.����λͼ���ű���
//width��hight��Ϊԭ�����ķ�һ���ò�����ʹ��2����������,��Ҳ��С��λͼռ�õ��ڴ��С�����磬һ��//�ֱ���Ϊ2048*1536px��ͼ��ʹ��inSampleSizeֵΪ4�����������룬������Bitmap��СԼΪ//512*384px�����������ͼƬռ��12M���ڴ棬���ַ�ʽֻ��0.75M�ڴ�(����Bitmap����Ϊ//ARGB_8888)��

		//Camera.Size size1=camera.getParameters().getPreviewSize();
//	 opts.outHeight=size1.height;
//	 opts.outWidth=size1.width;

//		opts.inSampleSize =(int) (opts.outHeight/mScreenHeight);
//6.���ý���λͼ�ĳߴ���Ϣ
		opts.inInputShareable = true;
		opts.inJustDecodeBounds=false;
		return  opts;
	}
	/*
	 * // �ҶȻ�
	 */
	public void greyScreen() throws IOException {
		String path = Environment.getExternalStorageDirectory()
				+ File.separator + "KuaiDiBangShou" + File.separator
				+ "greyTemp.jpg";

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(path,options);

		BitmapFactory.Options opts=getOption(options);
		// //��ת
		 screenBitmap = BitmapFactory.decodeFile(path,opts);
//		if (bitmap.getWidth() > bitmap.getHeight()) {
//			bitmap1 = ImageThumbnail.rotateBitmapByDegree(bitmap, 90);
//		}
//
//		// //��ȡ
//		//
//		float scaleLeft = preferences.getFloat("positionLeft", (float) 1.0 / 6);
//		float scaleTop = preferences.getFloat("positionTop", (float) 5.0 / 30);
//		// float scaleHeight = bitmap1.getHeight() / mScreenHeight;
//		// float scaleWidth=bitmap.getWidth()/mScreenWidth;
//		imageProcess.noequl("scaleLeft ", scaleLeft);
//		imageProcess.noequl("scaleTop ", scaleTop);
//		// imageProcess.noequl("(int)( bitmap.getWidth()*scaleLeft = ",(int)(
//		// bitmap.getWidth()*scaleLeft));
//
//		Bitmap screenBitmap = Bitmap.createBitmap(bitmap1,
//				(int) (bitmap.getWidth() * scaleLeft),
//				(int) (bitmap1.getHeight() * scaleTop),
//				(int) (bitmap1.getWidth() * (1 - 2 * scaleLeft)),
//				(int) ((0.25 - scaleTop) * bitmap1.getHeight() * 2));
//		imageProcess.noequl("getHeight()= ",
//				((0.25 - scaleTop) * bitmap1.getHeight()));


		imageProcess imageprocess = new imageProcess();

//		label.setText("ͼ��height��"
//				+ (int) ((0.25 - scaleTop) * bitmap1.getHeight() * 2)
//				+ "  width:" + (int) (bitmap1.getWidth() * (1 - 2 * scaleLeft)));
		// savePic(screenBitmap);
		// ת��Ϊ�Ҷ�ͼ
		// greyBitmap = imageprocess.RGBToGrey(screenBitmap);
		greyBitmap = imageprocess.greyToArray(screenBitmap);
		iv_photo.setImageBitmap(screenBitmap);
		width = greyBitmap.getWidth();
		height = greyBitmap.getHeight();

		String filePath = Environment.getExternalStorageDirectory()
				+ File.separator + "KuaiDiBangShou" + File.separator + "Data";
		String strNum = filePath + "//num2";
		String strWin2 = filePath + "//win2.dat";
		String strWhi2 = filePath + "//whi2.dat";
		String strModel = filePath + "//model14.dat";

		String strResult=filePath+"//result.txt";

		// ȡ����ȡ�ĻҶ�ͼ
		pixels = new int[width * height];
		// ��������ڴ����
		greyBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		by = new int[width * height];
		// int[] by1 = new int[width * height];
		for (int i = 0; i < width * height; i++) {
			by[i] = (0xff & pixels[i]);
		}


		SharedPreferences preferences = getSharedPreferences("set",
				MODE_PRIVATE);
		Editor editor = preferences.edit();
		int count = preferences.getInt("count", 1) + 1;
		editor.putInt("count", count);
		editor.commit();

		int sum = preferences.getInt("countSum", 0);
		if (count > sum) {
//			Toast.makeText(this, "ʹ�ô����ѹ�,�����ʹ�ã��빺��", 1).show();
			return;
		} else {

			int numlen = preferences.getInt("numLen", 0);

			String phoneNum =callint(by, width, height, strNum, strWin2,
					strWhi2, strModel, numlen);
			//	saveResultToSD(phoneNum,strResult);

			// for(int i=0;i<500;i++){
			// System.out.println(by[i]);
			// }
			int numPixels[] = new int[width * height];

			for (int p = 0; p < height; p++) {
				for (int q = 0; q < width; q++) {
					int gray = by[p * width + q];
					int newcolor = (gray << 16) | (gray << 8) | (gray);
					numPixels[p * width + q] = newcolor;
				}
			}
			Bitmap binaryBitmap = Bitmap.createBitmap(numPixels, 0, width,
					width, height, Bitmap.Config.RGB_565);
			iv_2.setImageBitmap(binaryBitmap);
			tv_num.setVisibility(View.VISIBLE);
			String numStr = phoneNum.trim();
			if (numStr.length() != 0) {
				imageProcess.noequl(numStr, 1);
				tv_num.setText(numStr);
				tv_num.setSelection(numStr.length());
			} else {
				tv_num.setText("");
			}
		}
	}

	public Bitmap createBitmap(Bitmap bitmap, int count, int num) {
		int width = bitmap.getWidth() / count;
		int hight = bitmap.getHeight();
		return Bitmap.createBitmap(bitmap, num * width, 0, width, hight);
	}

	public void savePic(View v) {
		View view = this.getWindow().getDecorView();
		view.buildDrawingCache();
		view.setDrawingCacheEnabled(true);
		// ��ȡ״̬���߶�
		Rect rect = new Rect();
		view.getWindowVisibleDisplayFrame(rect);
		int statusBarHeight = rect.top;

		Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0,
				statusBarHeight, (int) mScreenWidth - 2, (int) mScreenHeight
						- statusBarHeight - 300);

		savePic(bitmap);
		view.destroyDrawingCache();
	}

	/*
	 * ����ͼƬ
	 */
	public void savePic(Bitmap bitmap) {
		String filePath = Environment.getExternalStorageDirectory()
				+ File.separator + "KuaiDiBangShou" + File.separator
				+ "ScreenPicture";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss",
				Locale.US);
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		final String fname = file + File.separator + sdf.format(new Date())
				+ ".png";
		// View view = v.getRootView();
		// view.setDrawingCacheEnabled(true);
		// view.buildDrawingCache();
		// final Bitmap bitmap = view.getDrawingCache();
		// FileOutputStream out = null;
		if (bitmap != null) {
			try {
				FileOutputStream out;
				out = new FileOutputStream(fname);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//				Toast.makeText(processActivity.this, "ͼƬ�ѱ���", 1).show();
			} catch (Exception e) {
				System.out.println("file not write!");
			}
		} else {
			System.out.println("bitmap is NULL!");
		}
	}

	/*
	 * �������ļ�д���ֻ�sd��
	 */
	private void assetsDataToSD(String filePath, String fileName)
			throws IOException {
		InputStream myInput;
		myInput = this.getAssets().open(fileName);

		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		// �ж�sd���Ƿ����
		if (sdCardExist) {
			sdDir = new File(filePath);// ��ȡ��Ŀ¼
		} else {
//			Toast.makeText(this, "û���ҵ�sd��", 1).show();
		}
		if (!sdDir.exists()) {
			sdDir.mkdirs();
		}
		// File dir_file=new
		// File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),fileName);
		OutputStream myOutput = new FileOutputStream(sdDir + "//" + fileName);

		byte[] buffer = new byte[1024];
		int length = myInput.read(buffer);
		while (length > 0) {
			myOutput.write(buffer, 0, length);
			length = myInput.read(buffer);
		}
		myOutput.flush();
		myInput.close();
		myOutput.close();
	}

	public void saveResultToSD(String num, String filePath) throws IOException {
		File file = new File(filePath);
		FileWriter myOutput = new FileWriter(file,true);
		if (!file.exists()) {
			file.mkdirs();
			}
		myOutput.write("�绰��"+num+"\r\t");	
		
		myOutput.flush();
		myOutput.close();
	}

	/*
	 * ����㷨
	 */
	public int otsu(int[] by, int w, int h, int x0, int y0, int dx, int dy) {
		// unsigned char *np; // ͼ��ָ��
		int thresholdValue = 1; // ��ֵ
		int ihist[] = new int[256]; // ͼ��ֱ��ͼ��256����
		int k; // various counters
		int n, n1, n2;
		double m1, m2, sum, csum, fmax, sb;
		// for (int i = 0; i < by.length; i++) {
		// int np = by[i];
		// ihist[np]++;
		// } //otsu(by, avga, width , height, width / 4, 0,width/2,0)
		for (int j = y0; j < y0 + dy; j++)
			for (int i = x0; i < x0 + dx; i++) {
				{
					try {
						int np = by[j * w + i];
						ihist[np]++;
					} catch (Exception e) {
						System.out.println("yuejie .........." + i + "  " + j);
					}
				}
			}

		sum = csum = 0.0;
		n = 0;
		for (k = 0; k <= 255; k++) {
			sum += (double) k * (double) ihist[k]; // x*f(x) ������
			n += ihist[k]; // f(x) ����
		}
		if (n == 0) {
			return (160);
		}
		fmax = -1.0;
		n1 = 0;
		for (k = 0; k <= 255; k++) {
			n1 += ihist[k];
			if (n1 == 0) {
				continue;
			}
			n2 = n - n1;
			if (n2 == 0) {
				break;
			}
			csum += (double) k * ihist[k];
			m1 = csum / n1;
			m2 = (sum - csum) / n2;
			sb = (double) n1 * (double) n2 * (m1 - m2) * (m1 - m2);
			/**//* bbg: note: can be optimized. */
			if (sb > fmax) {
				fmax = sb;
				thresholdValue = k;
			}
		}
		return thresholdValue;// (thresholdValue);
	}

	/*
	 * ���Ͷ���
	 */
	public void sendMsg(View v) {
		String number = tv_num.getText().toString();
		String context = content;
		// SmsManager manager=SmsManager.getDefault();
		// ArrayList<String> texts=manager.divideMessage(context);
		// for(String text:texts){
		// manager.sendTextMessage(number, null, text, null, null);
		// }
//		// Toast.makeText(getApplicationContext(), "send success",1).show();
		Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
		sendIntent.setData(Uri.parse("smsto:" + number));
		sendIntent.putExtra("sms_body", context);
		startActivity(sendIntent);
	}

	/*
	 * ��绰
	 */
	public void callphone(View v) {
//		String number = tv_num.getText().toString();
//		// Intent intent=new Intent();
//		// intent.setAction("android.intent.action.CALL");
//		// intent.addCategory("android.intent.category.DEFA")
//		Intent intent = new Intent(Intent.ACTION_CALL);
//		intent.setData(Uri.parse("tel:" + number));
//		startActivity(intent);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		processActivity.this.finish();
		super.onBackPressed();
		destoryView();

	}

	public void back(View v){
		destoryView();
	  processActivity.this.finish();
		super.onBackPressed();
	}
	void destoryView(){
		if(iv_photo!=null){
			iv_photo=null;
		}
		if(iv_2!=null){
			iv_2=null;
		}
		if(greyBitmap!=null){
			greyBitmap.recycle();
			greyBitmap=null;
		}
		if(screenBitmap!=null){
			screenBitmap.recycle();
			screenBitmap=null;
		}
		if(tv_num!=null){
			tv_num=null;
		}

	}
}
