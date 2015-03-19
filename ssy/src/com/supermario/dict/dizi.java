package com.supermario.dict;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
public class dizi extends Activity implements OnClickListener, TextWatcher
{
	//�������ݿ�Ĵ��·��
	private final String DATABASE_PATH = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/dictionary";
	//�û������ı���
	private AutoCompleteTextView word;
	//�������ݿ������
	private final String DATABASE_FILENAME = "dictionary.db";
	private SQLiteDatabase database;
	//������ť
	private Button searchWord;
	//�û���ʾ��ѯ���
	private TextView showResult;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//�����ݿ�
		database = openDatabase();
		searchWord = (Button) findViewById(R.id.searchWord);
		word = (AutoCompleteTextView) findViewById(R.id.word);
		//�󶨼�����
		searchWord.setOnClickListener(this);
		//�����ָı������
		word.addTextChangedListener(this);
		showResult=(TextView)findViewById(R.id.result);
	}
	//�Զ���Adapter��
	public class DictionaryAdapter extends CursorAdapter
	{
		private LayoutInflater layoutInflater;
		@Override
		public CharSequence convertToString(Cursor cursor)
		{
			return cursor == null ? "" : cursor.getString(cursor
					.getColumnIndex("_id"));
		}
		//��������Ϣ��ʾ���б���
		private void setView(View view, Cursor cursor)
		{
			TextView tvWordItem = (TextView) view;
			tvWordItem.setText(cursor.getString(cursor.getColumnIndex("_id")));
		}
		//��ѡ��б���
		@Override
		public void bindView(View view, Context context, Cursor cursor)
		{
			setView(view, cursor);
		}
		//�����µ�ѡ��
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent)
		{
			View view = layoutInflater.inflate(R.layout.word_list_item, null);
			setView(view, cursor);
			return view;
		}
		public DictionaryAdapter(Context context, Cursor c, boolean autoRequery)
		{
			super(context, c, autoRequery);
			layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
	}
	public void afterTextChanged(Editable s)
	{	
        //  ���뽫english�ֶεı�����Ϊ_id 
		Cursor cursor = database.rawQuery(
				"select english as _id from t_words where english like ?",
				new String[]
				{ s.toString() + "%" });
		//�½��µ�Adapter
		DictionaryAdapter dictionaryAdapter = new DictionaryAdapter(this,
				cursor, true);
		//��������
		word.setAdapter(dictionaryAdapter);

	}
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after)
	{
		// TODO Auto-generated method stub

	}
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		// TODO Auto-generated method stub

	}
	public void onClick(View view)
	{
		//��ѯָ���ĵ���
		String sql = "select chinese from t_words where english=?";		
		Cursor cursor = database.rawQuery(sql, new String[]
		{word.getText().toString()});
			String result = "δ�ҵ��õ���.";
			//  ������ҵ��ʣ���ʾ�����ĵ���˼
			if (cursor.getCount() > 0)
			{
				//  ����ʹ��moveToFirst��������¼ָ���ƶ�����1����¼��λ��
				cursor.moveToFirst();
				result = cursor.getString(cursor.getColumnIndex("chinese")).replace("&amp;", "&");
			}
			//�������ʾ��TextView��
			showResult.setText(word.getText()+"\n"+result.toString());
	}
	private SQLiteDatabase openDatabase()
	{
		try
		{
			// ���dictionary.db�ļ��ľ���·��
			String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
			File dir = new File(DATABASE_PATH);
			// ���/sdcard/dictionaryĿ¼�д��ڣ��������Ŀ¼
			if (!dir.exists())
				dir.mkdir();
			// �����/sdcard/dictionaryĿ¼�в�����
			// dictionary.db�ļ������res\rawĿ¼�и�������ļ���
			// SD����Ŀ¼��/sdcard/dictionary��
			if (!(new File(databaseFilename)).exists())
			{
				// ��÷�װdictionary.db�ļ���InputStream����
				InputStream is = getResources().openRawResource(
						R.raw.dictionary);
				FileOutputStream fos = new FileOutputStream(databaseFilename);
				byte[] buffer = new byte[8192];
				int count = 0;
				// ��ʼ����dictionary.db�ļ�
				while ((count = is.read(buffer)) > 0)
				{
					fos.write(buffer, 0, count);
				}
				//�ر��ļ���
				fos.close();
				is.close();
			}
			// ��/sdcard/dictionaryĿ¼�е�dictionary.db�ļ�
			SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
					databaseFilename, null);
			return database;
		}
		catch (Exception e)
		{
		}
		//����򿪳������򷵻�null
		return null;
	}
}