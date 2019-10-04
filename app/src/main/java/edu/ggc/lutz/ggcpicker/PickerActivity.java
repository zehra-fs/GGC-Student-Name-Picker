package edu.ggc.lutz.ggcpicker;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

//Zehra Siddiqui
public class PickerActivity extends AppCompatActivity {

    private static final int OCR_ACTIVITY = 102;
    private static final String TAG = "GGC";
    private ListView listV;
    //private ArrayList<String> studentList;
    private ArrayList<String> list;

    private ArrayAdapter<String> student_adapter;
    private String m_Text = "";
    private TextView studentPicked;
    TextToSpeech spokentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        studentPicked = findViewById(R.id.tvPicked);

        list = new ArrayList<String>();
        listV = (ListView) findViewById(R.id.lvStudents);
        student_adapter = new ArrayAdapter<>
                (this,android.R.layout.simple_list_item_1,list);
        listV.setAdapter(student_adapter);
        registerForContextMenu(listV);
        spokentText = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR)
                {
                    spokentText.setLanguage(Locale.ENGLISH);
                }
            }
        });
/*
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        if (savedInstanceState != null) {
            String wordsString = sharedPref.getString("listview", "");
            String[] itemWords = wordsString.split(",");
            ArrayList<String> studentList2 = new ArrayList();
            for (int i = 0; i < itemWords.length; i++) {
                studentList2.add(itemWords[i]);
            }
            list = studentList2;
            for (int i = 0; i < studentList2.size(); i++) {
                Log.d("listItem", list.get(i));
            }
            listV = (ListView) findViewById(R.id.lvStudents);
            student_adapter = new ArrayAdapter<>
                    (this,android.R.layout.simple_list_item_1,list);
            listV.setAdapter(student_adapter);
            registerForContextMenu(listV);
        }
 */
        FloatingActionButton fab = findViewById(R.id.fabRoll);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if(prefs.getBoolean("vibration_switch", false) == true)
                {
                    // Get instance of Vibrator from current Context
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                    // Vibrate for 400 milliseconds
                    vibrator.vibrate(400);
                }
                ArrayList presentStudents = new ArrayList();
                for (int i = 0; i < list.size(); i++)
                {
                    if (listV.getChildAt(i).isEnabled() == true)
                    {
                        presentStudents.add(list.get(i));
                    }
                }
                int rand = new Random().nextInt(presentStudents.size());
                studentPicked.setText((String) presentStudents.get(rand));
                if (prefs.getBoolean("tts_switch", false) == true)
                {
                    spokentText.speak(studentPicked.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                }

            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OCR_ACTIVITY && resultCode == Activity.RESULT_OK) {
            List<String> add = (ArrayList<String>) data.getSerializableExtra("ocr_list");
            list.addAll(add);
            //student_adapter.notifyDataSetChanged();

            try {
                // open file
                Log.i(TAG, "directory location = " + getFilesDir());
                FileOutputStream out = openFileOutput("itec4550-text.txt", Context.MODE_PRIVATE);
                PrintWriter writer = new PrintWriter(out);

                // write file
                for(String str: list)
                    writer.println(str);


                // close
                writer.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            list.clear();
           /*  for (String s : list)
            {
                //Log.i(TAG, "student = " + s);
           */
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_picker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_ocr_camera: {
                Intent intent = new Intent(this, OCRActivity.class);
                startActivityForResult(intent, OCR_ACTIVITY);
                break;
            }

            case R.id.addName: {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Add Name");
                builder.setMessage("Add a new name to the list:");
// Set up the input
                final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        //listV = findViewById(R.id.lvStudents);
                        // listV.add(m_Text);
//                        if (list == null)
//                        {
//                            list = new ArrayList<>();
//                        }
                        list.add(m_Text);
                        student_adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                break;
            }

            case R.id.clearList:
            {
                list.clear();
                student_adapter.notifyDataSetChanged();
                break;
            }

            case R.id.shuffleList:
            {
                Collections.shuffle(list);
                student_adapter.notifyDataSetChanged();
                break;
            }

            case R.id.sortList:
            {
                Collections.sort(list);
                student_adapter.notifyDataSetChanged();
                break;
            }

            case R.id.about:
            {
                Intent intent = new Intent(this, About.class);
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()== R.id.lvStudents) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(list.get(info.position));
            String[] menuItems = getResources().getStringArray(R.array.longPressMenu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
                Log.i(TAG, menuItems[i] );
            }

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        //Log.i("menuList", Integer.toString(menuItemIndex));
        switch (menuItemIndex)
        {
            case 0: //If Delete is picked
            {
                list.remove(info.position);
                student_adapter.notifyDataSetChanged();
                break;
            }

            case 1: //If Hide/Unhide is picked
            {
                if(listV.getChildAt(info.position).isEnabled())
                {
                    listV.getChildAt(info.position).setEnabled(false);
                }
                else
                    listV.getChildAt(info.position).setEnabled(true);
                break;
            }

        }


//        String[] menuItems = getResources().getStringArray(R.array.longPressMenu);
//        String menuItemName = menuItems[menuItemIndex];
//        String listItemName = list.get(info.position);

//        TextView text = (TextView)findViewById(R.id.footer);
//        text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        for (Map.Entry<String, ?> entry : prefs.getAll().entrySet())
            Log.d("SETTINGS", entry.getKey() + ": " + entry.getValue().toString());
        Log.d("SETTINGS", "switch = " + prefs.getBoolean("example_switch", false));
        list.clear();
        try {
            // open the file
           // list.clear();
            FileInputStream fis = openFileInput("itec4550-text.txt");
            Scanner scanner = new Scanner(fis);

            // process
            while(scanner.hasNext()) {
                String line = scanner.nextLine();
                Log.i(TAG, "Line = " + line);
                list.add(line);
            }

            // close the file
            scanner.close();
            Log.i(TAG, "listSize =" + list.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

            student_adapter.notifyDataSetChanged();

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            // open file
            Log.i(TAG, "directory location = " + getFilesDir());
            FileOutputStream out = openFileOutput("itec4550-text.txt", Context.MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(out);

            // write file
            for(String str: list)
                writer.println(str);


            // close
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

         /*SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putInt("count", mCount);
        preferencesEditor.putInt("color", mCurrentColor);
        preferencesEditor.apply(); */
    }

    /*@Override
    protected void onStop()
    {
        super.onStop();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("listview", list.toString());
        editor.commit();
    }

 */

}
