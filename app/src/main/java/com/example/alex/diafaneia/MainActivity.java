package com.example.alex.diafaneia;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex.diafaneia.Model.Document;
import com.example.alex.diafaneia.Model.Result;
import com.example.alex.diafaneia.Model.Sector;
import com.example.alex.diafaneia.Model.Signer;
import com.example.alex.diafaneia.Model.Type;
import com.example.alex.diafaneia.Utils.Constants;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static Sector sector = null;
    private static Document document = null;
    private static Type type = null;
    private static Signer signer = null;
    private static String ADA = null;
    private static String protoc_Num = null;
    private static String free_Text_str = null;
    private static String fromDate = null;
    private static String toDate = null;


    static Result r = null;

    private EditText fromDateEtxt;
    private EditText toDateEtxt;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private SimpleDateFormat dateFormatter;

    private TextView text_Sector;
    private TextView text_Doc;
    private TextView text_Type;
    private TextView text_Sign;
    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private TextView protocol_Num;
    private TextView free_Text;
    private ImageView c1;
    private ImageView c2;
    private ImageView c3;
    private ImageView c4;
    private ImageView c5;
    private ImageView c6;
    private ImageView c7;
    private ImageView c8;
    private ImageView c9;
    private ImageView info_button;
    private ImageView bookmark_button;
    private RelativeLayout RL1;

    private Button firstButton;
    private Button secondButton;
    private Button thirdButton;
    private Button fourthButton;

    private ImageView clear_b;
    private ImageView search_b;


    // Action Listeners
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        findViewsById();


        // Date Pickers
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        fromDateEtxt.setInputType(InputType.TYPE_NULL);
        toDateEtxt.setInputType(InputType.TYPE_NULL);
        setDateTimeField();

        Date cDate = new Date();
        final String fDate = new SimpleDateFormat("dd/MM/yyyy").format(cDate);
        toDateEtxt.setText(fDate);


//        // Unfocus from Edit Texts
//        RL1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                InputMethodManager imm = (InputMethodManager) view.getContext()
//                        .getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//
//            }
//        });

        //updateTextFields
        //SECTOR
        if (sector == null) {
            text_Sector.setText(Constants.SECTOR_TITLE);
            text_Sector.setTextColor(Color.parseColor("#7E7E7E"));
            c1.setImageResource(R.drawable.cancel_button_inactive);
        } else {                                                      // else : the user has entered values(selected from list)
            text_Sector.setText(sector.getSectorTitle());
            text_Sector.setTextColor(Color.parseColor("#333333"));
            c1.setImageResource(R.drawable.cancel_button_active);
        }
        //DOCUMENT
        if (document == null) {
            text_Doc.setText(Constants.DOCUMENT_TITLE);
            text_Doc.setTextColor(Color.parseColor("#7E7E7E"));
            c2.setImageResource(R.drawable.cancel_button_inactive);
        } else {
            text_Doc.setText(document.getDocumentTitle());
            text_Doc.setTextColor(Color.parseColor("#333333"));
            c2.setImageResource(R.drawable.cancel_button_active);
        }
        //TYPE
        if (type == null) {
            text_Type.setText(Constants.TYPE_TITLE);
            text_Type.setTextColor(Color.parseColor("#7E7E7E"));
            c3.setImageResource(R.drawable.cancel_button_inactive);
        } else {
            text_Type.setText(type.getTypeTitle());
            text_Type.setTextColor(Color.parseColor("#333333"));
            c3.setImageResource(R.drawable.cancel_button_active);
        }
        //SIGNER
        if (signer == null) {
            text_Sign.setText(Constants.SIGNER_TITLE);
            text_Sign.setTextColor(Color.parseColor("#7E7E7E"));
            c4.setImageResource(R.drawable.cancel_button_inactive);

        } else {
            text_Sign.setText(signer.getSignerFullName());
            text_Sign.setTextColor(Color.parseColor("#333333"));
            c4.setImageResource(R.drawable.cancel_button_active);
        }


        //ADA
        if (ADA != null) {
            c5.setImageResource(R.drawable.cancel_button_active);
        }
        //
        if (protoc_Num != null) {
            c6.setImageResource(R.drawable.cancel_button_active);
        }//
        if (free_Text_str != null) {
            c7.setImageResource(R.drawable.cancel_button_active);
        }
//        if (fromDate != null) {
//            c8.setImageResource(R.drawable.cancel_button_active);
//        }
//        if (toDate != null) {
//            c9.setImageResource(R.drawable.cancel_button_active);
//        }

        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                c1.setImageResource(R.drawable.cancel_button_inactive);
                TextView text_Sector = (TextView) findViewById(R.id.text1);
                text_Sector.setText(Constants.SECTOR_TITLE);
                text_Sector.setTextColor(Color.parseColor("#7E7E7E"));
                sector = null;
            }

        });

        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                c2.setImageResource(R.drawable.cancel_button_inactive);
                TextView text_Doc = (TextView) findViewById(R.id.text2);
                text_Doc.setText(Constants.DOCUMENT_TITLE);
                text_Doc.setTextColor(Color.parseColor("#7E7E7E"));
                document = null;
                if (type != null) {
                    c3.setImageResource(R.drawable.cancel_button_inactive);
                    text_Type.setTextColor(Color.parseColor("#333333"));
                    text_Type.setText(Constants.TYPE_TITLE);
                    text_Type.setTextColor(Color.parseColor("#7E7E7E"));
                    type = null;
                }

            }

        });

        c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                c3.setImageResource(R.drawable.cancel_button_inactive);
                TextView text_Type = (TextView) findViewById(R.id.text3);
                text_Type.setText(Constants.TYPE_TITLE);
                text_Type.setTextColor(Color.parseColor("#7E7E7E"));
                type = null;
            }

        });

        c4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                c4.setImageResource(R.drawable.cancel_button_inactive);
                TextView text_Sign = (TextView) findViewById(R.id.text4);
                text_Sign.setText(Constants.SIGNER_TITLE);
                text_Sign.setTextColor(Color.parseColor("#7E7E7E"));
                signer = null;
            }

        });

        c5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c5.setImageResource(R.drawable.cancel_button_inactive);
                editText1.setText(null);
                ADA = null;

                InputMethodManager imm = (InputMethodManager) RL1.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(RL1.getWindowToken(), 0);
                editText1.setCursorVisible(false);
            }

        });
        c6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                c6.setImageResource(R.drawable.cancel_button_inactive);
                editText2.setText(null);
                protoc_Num = null;

                InputMethodManager imm = (InputMethodManager) RL1.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(RL1.getWindowToken(), 0);
                editText2.setCursorVisible(false);
            }

        });
        c7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c7.setImageResource(R.drawable.cancel_button_inactive);
                editText3.setText(null);
                free_Text_str = null;

                InputMethodManager imm = (InputMethodManager) RL1.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(RL1.getWindowToken(), 0);
                editText3.setCursorVisible(false);
            }

        });
        c8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c8.setImageResource(R.drawable.cancel_button_inactive);
                EditText text = (EditText) findViewById(R.id.etxt_fromdate);
                text.setText("1/1/2014");
                fromDate = null;

//                InputMethodManager imm = (InputMethodManager) RL1.getContext()
//                        .getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(RL1.getWindowToken(), 0);
//                text.setCursorVisible(false);
            }

        });
        c9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c9.setImageResource(R.drawable.cancel_button_inactive);
                EditText text = (EditText) findViewById(R.id.etxt_todate);
                toDateEtxt.setText(fDate);
                toDate = null;

//                InputMethodManager imm = (InputMethodManager) RL1.getContext()
//                        .getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(RL1.getWindowToken(), 0);
//                text.setCursorVisible(false);
            }

        });
        info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Info.class);
                startActivity(intent);
            }

        });

        bookmark_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Bookmark.class);
                startActivity(intent);
            }

        });


        firstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityTwo.class);
                intent.putExtra("B", Constants.SECTOR_TITLE);
                startActivity(intent);
            }

        });

        secondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityTwo.class);
                intent.putExtra("B", Constants.DOCUMENT_TITLE);
                startActivity(intent);
            }

        });

        thirdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (document != null) {
                    Intent intent = new Intent(getApplicationContext(), ActivityTwo.class);
                    intent.putExtra("B", Constants.TYPE_TITLE);
                    startActivity(intent);
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "Επιλέξτε πρώτα Θεματική Ενότητα";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }

        });

        fourthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityTwo.class);
                intent.putExtra("B", Constants.SIGNER_TITLE);
                startActivity(intent);
            }

        });
        cancelButton(editText1);
        cancelButton(editText2);
        cancelButton(editText3);

        search_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r = new Result(sector, document, type, signer, ADA, protoc_Num, free_Text_str, fromDate, toDate);
                Intent intent = new Intent(getApplicationContext(), Results_Activity.class);
                startActivity(intent);
            }

        });
        clear_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                c1.performClick();
                c2.performClick();
                c3.performClick();
                c4.performClick();
                c5.setImageResource(R.drawable.cancel_button_inactive);
                editText1.setText(null);
                ADA = null;
                c6.setImageResource(R.drawable.cancel_button_inactive);
                editText2.setText(null);
                protoc_Num = null;
                c7.setImageResource(R.drawable.cancel_button_inactive);
                editText3.setText(null);
                free_Text_str = null;
                c8.setImageResource(R.drawable.cancel_button_inactive);
                EditText text = (EditText) findViewById(R.id.etxt_fromdate);
                text.setText("1/1/2014");
                fromDate = null;
                c9.setImageResource(R.drawable.cancel_button_inactive);
                EditText text2 = (EditText) findViewById(R.id.etxt_todate);
                text2.setText(fDate);
                toDate = null;

            }

        });

    }

    private void findViewsById() {
        //textViews and edit texts
        text_Sector = (TextView) findViewById(R.id.text1);
        text_Doc = (TextView) findViewById(R.id.text2);
        text_Type = (TextView) findViewById(R.id.text3);
        text_Sign = (TextView) findViewById(R.id.text4);
        editText1 = (EditText) this.findViewById(R.id.editText1);
        editText2 = (EditText) this.findViewById(R.id.editText2);
        editText3 = (EditText) this.findViewById(R.id.editText3);
        protocol_Num = (TextView) findViewById(R.id.editText2);
        free_Text = (TextView) findViewById(R.id.editText3);

        //Date Picker
        fromDateEtxt = (EditText) findViewById(R.id.etxt_fromdate);
        toDateEtxt = (EditText) findViewById(R.id.etxt_todate);

        clear_b = (ImageView) findViewById(R.id.clear_btn);
        search_b = (ImageView) findViewById(R.id.search_btn);

        //cancel buttons
        c1 = (ImageView) findViewById(R.id.cancel1);
        c2 = (ImageView) findViewById(R.id.cancel2);
        c3 = (ImageView) findViewById(R.id.cancel3);
        c4 = (ImageView) findViewById(R.id.cancel4);
        c5 = (ImageView) findViewById(R.id.cancel5);
        c6 = (ImageView) findViewById(R.id.cancel6);
        c7 = (ImageView) findViewById(R.id.cancel7);
        c8 = (ImageView) findViewById(R.id.cancel_date_2);
        c9 = (ImageView) findViewById(R.id.cancel_date_1);

        //Info and Bookmark Buttons
        info_button = (ImageView) findViewById(R.id.info_btn);
        bookmark_button=(ImageView)findViewById(R.id.bookmark_btn);
        
        // Unfocus from Edit Texts
        RL1 = (RelativeLayout) findViewById(R.id.RL1);


        firstButton = (Button) findViewById(R.id.button1);
        secondButton = (Button) findViewById(R.id.button2);
        thirdButton = (Button) findViewById(R.id.button3);
        fourthButton = (Button) findViewById(R.id.button4);
    }

    public static void setSector(Sector sec) {
        MainActivity.sector = sec;
    }

    public static void setDocument(Document document) {
        MainActivity.document = document;
    }

    public static void setType(Type type) {
        MainActivity.type = type;
    }

    public static void setSigner(Signer signer) {
        MainActivity.signer = signer;
    }

    public static Document getDocument() {
        return document;
    }


    //Emulate Home Button
    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void setDateTimeField() {
        fromDateEtxt.setOnClickListener(this);
        toDateEtxt.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));

                fromDate = fromDateEtxt.getText().toString();
                fromDate = fromDate.replace("-", "%2F");
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                toDateEtxt.setText(dateFormatter.format(newDate.getTime()));

                toDate = toDateEtxt.getText().toString();
                toDate = toDate.replace("-", "%2F");
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }


    @Override
    public void onClick(View view) {

        if (view == fromDateEtxt) {
            fromDatePickerDialog.show();
        } else if (view == toDateEtxt) {
            toDatePickerDialog.show();
        }
    }

    public void cancelButton(final EditText text) {
        int test = 0;
        if (text.getId() == editText1.getId()) {
            test = 1;
            if (ADA != null) {
                text.setText(ADA);
            }
        } else if (text.getId() == editText2.getId()) {
            test = 2;
            if (protoc_Num != null) {
                text.setText(protoc_Num);
            }
        } else if (text.getId() == editText3.getId()) {
            test = 3;
            if (free_Text_str != null) {
                text.setText(free_Text_str);
            }
        }

        text.setTextColor(Color.parseColor("#3B3B3B"));
        text.setTextSize(13);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == text.getId()) {
                    text.setCursorVisible(true);

                }
                text.setTextColor(Color.parseColor("#3B3B3B"));
                text.setTextSize(13);

            }
        });

        final int finalTest = test;
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent event) {
                text.setCursorVisible(false);
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(text.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
                String temp = text.getText().toString();
                if (temp.trim().length() > 0) {
                    if (finalTest == 1) {
                        ADA = temp;
                        c5.setImageResource(R.drawable.cancel_button_active);
                    } else if (finalTest == 2) {
                        protoc_Num = temp;
                        c6.setImageResource(R.drawable.cancel_button_active);
                    } else if (finalTest == 3) {
                        free_Text_str = temp;
                        c7.setImageResource(R.drawable.cancel_button_active);
                    }

                }
                return false;
            }

        });
    }

    public static Result getR() {
        return r;
    }




}



