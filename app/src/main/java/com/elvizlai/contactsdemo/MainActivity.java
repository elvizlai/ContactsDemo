package com.elvizlai.contactsdemo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AlphabetIndexer;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    /**
     * 数据库
     */
    DBHelper dbHelper;

    /**
     * 分组的布局
     */
    private LinearLayout titleLayout;

    /**
     * 弹出式分组的布局
     */
    private RelativeLayout sectionToastLayout;

    /**
     * 右侧可滑动字母表
     */
    private Button alphabetButton;

    /**
     * 分组上显示的字母
     */
    private TextView title;

    /**
     * 弹出式分组上的文字
     */
    private TextView sectionToastText;

    /**
     * 联系人ListView
     */
    private ListView contactsListView;

    /**
     * 联系人列表适配器
     */
    private ContactAdapter adapter;

    /**
     * 用于进行字母表分组
     */
    private AlphabetIndexer indexer;

    /**
     * 存储所有手机中的联系人
     */
    private List<Contact> contacts = new ArrayList<Contact>();

    /**
     * 定义字母表的排序规则
     */
    private String alphabet = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 上次第一个可见元素，用于滚动时记录标识。
     */
    private int lastFirstVisibleItem = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        insertAndUpdateData(dbHelper);

        adapter = new ContactAdapter(this, R.layout.contact_item, contacts);
        titleLayout = (LinearLayout) findViewById(R.id.title_layout);
        sectionToastLayout = (RelativeLayout) findViewById(R.id.section_toast_layout);
        title = (TextView) findViewById(R.id.title);
        sectionToastText = (TextView) findViewById(R.id.section_toast_text);
        alphabetButton = (Button) findViewById(R.id.alphabetButton);
        contactsListView = (ListView) findViewById(R.id.contacts_list_view);

        Cursor cursor = dbHelper.getReadableDatabase().query("contacts", new String[]{"Name", "pinyinName"}, null, null, null, null, "pinyinName");

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                String sortKey = getSortKey(cursor.getString(1));
                Contact contact = new Contact();
                contact.setName(name);
                contact.setSortKey(sortKey);
                contacts.add(contact);
            } while (cursor.moveToNext());
        }

        startManagingCursor(cursor);

        //CursorLoader cursorLoader = new CursorLoader(this);
        //cursorLoader.

        indexer = new AlphabetIndexer(cursor, 1, alphabet);
        adapter.setIndexer(indexer);
        if (contacts.size() > 0) {
            setAlpabetListener();
            setupContactsListView();
        }


    }

    /**
     * 为联系人ListView设置监听事件，根据当前的滑动状态来改变分组的显示位置，从而实现挤压动画的效果。
     */
    private void setupContactsListView() {
        contactsListView.setAdapter(adapter);
        contactsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                int section = indexer.getSectionForPosition(firstVisibleItem);
                int nextSecPosition = indexer.getPositionForSection(section + 1);
                if (firstVisibleItem != lastFirstVisibleItem) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout.getLayoutParams();
                    params.topMargin = 0;
                    titleLayout.setLayoutParams(params);
                    title.setText(String.valueOf(alphabet.charAt(section)));
                }
                if (nextSecPosition == firstVisibleItem + 1) {
                    View childView = view.getChildAt(0);
                    if (childView != null) {
                        int titleHeight = titleLayout.getHeight();
                        int bottom = childView.getBottom();
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout
                                .getLayoutParams();
                        if (bottom < titleHeight) {
                            float pushedDistance = bottom - titleHeight;
                            params.topMargin = (int) pushedDistance;
                            titleLayout.setLayoutParams(params);
                        } else {
                            if (params.topMargin != 0) {
                                params.topMargin = 0;
                                titleLayout.setLayoutParams(params);
                            }
                        }
                    }
                }
                lastFirstVisibleItem = firstVisibleItem;
            }
        });

    }

    /**
     * 获取sort key的首个字符，如果是英文字母就直接返回，否则返回#。
     *
     * @param sortKeyString 数据库中读取出的sort key
     * @return 英文字母或者#
     */
    private String getSortKey(String sortKeyString) {
        String key = sortKeyString.substring(0, 1).toUpperCase();
        if (key.matches("[A-Z]")) {
            return key;
        }
        return "#";
    }


    private void insertAndUpdateData(DBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String nameList = "insert into contacts(Name,pinYinName,tel) values(?,?,?)";

        db.execSQL(nameList, new Object[]{"莫言", "莫言", "185"});
        db.execSQL(nameList, new Object[]{"刘德华", "liudehua", "185"});
        db.execSQL(nameList, new Object[]{"农夫山泉", "nongfushanquan", "185"});
        db.execSQL(nameList, new Object[]{"张根硕", "zhanggenshuo", "185"});
        db.execSQL(nameList, new Object[]{"牛根生", "niugensheng", "185"});
        db.execSQL(nameList, new Object[]{"史玉柱", "shiyuzhu", "185"});
        db.execSQL(nameList, new Object[]{"张三", "zhangsan", "185"});
        db.execSQL(nameList, new Object[]{"安然", "anran", "185"});
        db.execSQL(nameList, new Object[]{"碧波", "bibo", "185"});
        db.execSQL(nameList, new Object[]{"菜鸟", "cainiao", "185"});
        db.execSQL(nameList, new Object[]{"司马迁", "simaqian", "185"});
        db.execSQL(nameList, new Object[]{"黄渤", "huangbo", "185"});
        db.execSQL(nameList, new Object[]{"Baby", "baby", "185"});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setAlpabetListener() {
        alphabetButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float alphabetHeight = alphabetButton.getHeight();
                float y = event.getY();
                int sectionPosition = (int) ((y / alphabetHeight) / (1f / 27f));
                if (sectionPosition < 0) {
                    sectionPosition = 0;
                } else if (sectionPosition > 26) {
                    sectionPosition = 26;
                }
                String sectionLetter = String.valueOf(alphabet.charAt(sectionPosition));
                int position = indexer.getPositionForSection(sectionPosition);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        alphabetButton.setBackgroundResource(R.drawable.a_z_click);
                        sectionToastLayout.setVisibility(View.VISIBLE);
                        sectionToastText.setText(sectionLetter);
                        contactsListView.setSelection(position);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        sectionToastText.setText(sectionLetter);
                        contactsListView.setSelection(position);
                        break;
                    default:
                        alphabetButton.setBackgroundResource(R.drawable.a_z);
                        sectionToastLayout.setVisibility(View.GONE);
                }
                return true;
            }
        });
    }


}
