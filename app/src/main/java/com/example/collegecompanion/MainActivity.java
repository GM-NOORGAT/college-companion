package com.example.collegecompanion;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    final int navy=Color.rgb(23,37,84), blue=Color.rgb(37,99,235), bg=Color.rgb(243,246,251), ink=Color.rgb(23,32,51), muted=Color.rgb(100,116,139);
    LinearLayout content; android.content.SharedPreferences prefs;
    String page="Home";
    int dp(int n){return (int)(n*getResources().getDisplayMetrics().density);}
    TextView text(String s,int size,boolean bold,int color){
        TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        if(bold)t.setTypeface(null,Typeface.BOLD); t.setPadding(dp(3),dp(6),dp(3),dp(6)); return t;
    }
    Button button(String s,Runnable r){
        Button b=new Button(this); b.setText(s); b.setAllCaps(false); b.setTextColor(Color.WHITE);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(blue)); b.setOnClickListener(v->r.run()); return b;
    }
    EditText field(String hint,String value){
        EditText e=new EditText(this); e.setHint(hint); e.setText(value); e.setSingleLine(true); return e;
    }
    LinearLayout card(){
        LinearLayout c=new LinearLayout(this); c.setOrientation(1); c.setPadding(dp(14),dp(10),dp(14),dp(10)); c.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,dp(5),0,dp(5)); c.setLayoutParams(p); return c;
    }
    public void onCreate(Bundle b){super.onCreate(b); prefs=getSharedPreferences("college_companion",0); show("Home");}
    void show(String p){
        page=p; LinearLayout root=new LinearLayout(this); root.setOrientation(1); root.setBackgroundColor(bg);
        LinearLayout header=new LinearLayout(this); header.setOrientation(1); header.setPadding(dp(18),dp(12),dp(18),dp(12)); header.setBackgroundColor(navy);
        header.addView(text("STUDENT LIFE, SIMPLIFIED",11,true,Color.LTGRAY)); header.addView(text("🎓 College Companion",24,true,Color.WHITE));
        root.addView(header);
        ScrollView scroll=new ScrollView(this); content=new LinearLayout(this); content.setOrientation(1); content.setPadding(dp(14),dp(8),dp(14),dp(8)); scroll.addView(content);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        HorizontalScrollView hsv=new HorizontalScrollView(this); LinearLayout nav=new LinearLayout(this); nav.setBackgroundColor(Color.WHITE);
        String[] pages={"Home","Timetable","Notices","Assignments","Events"};
        for(String x:pages){TextView n=text(x,12,true,x.equals(p)?blue:muted); n.setGravity(Gravity.CENTER); n.setPadding(dp(12),dp(12),dp(12),dp(12)); n.setOnClickListener(v->show(x)); nav.addView(n);}
        hsv.addView(nav); root.addView(hsv); setContentView(root);
        switch(p){case "Home":home();break;case "Timetable":editorList("timetable","Timetable","Day","Time","Class / subject");break;
            case "Notices":editorList("notices","Notices","Title","Date","Details");break;
            case "Assignments":assignments();break;default:editorList("events","Events","Event name","Date","Location / details");}
    }
    void home(){
        content.addView(text("Welcome, Student 👋",22,true,ink));
        LinearLayout c=card(); c.addView(text("Your college essentials in one place.",15,false,muted));
        c.addView(text("📅 Timetable entries: "+count("timetable"),16,true,ink));
        c.addView(text("📢 Notices: "+count("notices"),16,true,ink));
        c.addView(text("✅ Assignments: "+count("assignments"),16,true,ink));
        c.addView(text("🎉 Events: "+count("events"),16,true,ink)); content.addView(c);
        c=card(); c.addView(text("Quick start",18,true,ink)); c.addView(button("＋ Add timetable entry",()->show("Timetable")));
        c.addView(button("＋ Add notice",()->show("Notices"))); c.addView(button("＋ Add assignment",()->show("Assignments")));
        c.addView(button("＋ Add event",()->show("Events"))); content.addView(c);
    }
    int count(String key){String a=prefs.getString(key,""); if(a.isEmpty())return 0; return a.split("\n",-1).length;}
    void editorList(String key,String heading,String aLabel,String bLabel,String cLabel){
        content.addView(text("✏️ "+heading,22,true,ink));
        content.addView(text("Add, edit or delete entries. Your changes are saved on this device.",13,false,muted));
        EditText a=field(aLabel,""), b=field(bLabel,""), c=field(cLabel,"");
        LinearLayout form=card(); form.addView(a); form.addView(b); form.addView(c);
        form.addView(button("Save new entry",()->{
            if(a.getText().toString().trim().isEmpty()){toast("Please enter "+aLabel.toLowerCase());return;}
            String row=enc(a.getText().toString())+"|"+enc(b.getText().toString())+"|"+enc(c.getText().toString());
            String old=prefs.getString(key,""); prefs.edit().putString(key,old.isEmpty()?row:old+"\n"+row).apply(); show(page);
        })); content.addView(form);
        String all=prefs.getString(key,""); if(all.isEmpty()){content.addView(text("No entries yet. Add one above.",14,false,muted));return;}
        int i=0; for(String row:all.split("\n")){final String original=row; String[] parts=row.split("\\|",-1); if(parts.length!=3)continue;
            final int idx=i++; LinearLayout item=card(); item.addView(text(dec(parts[0]),17,true,ink));
            if(!dec(parts[1]).isEmpty())item.addView(text(dec(parts[1]),14,true,blue));
            if(!dec(parts[2]).isEmpty())item.addView(text(dec(parts[2]),14,false,muted));
            LinearLayout actions=new LinearLayout(this);
            actions.addView(button("Edit",()->editEntry(key,heading,aLabel,bLabel,cLabel,original)));
            actions.addView(button("Delete",()->{removeRow(key,original);show(page);}));
            item.addView(actions); content.addView(item);
        }
    }
    void editEntry(String key,String heading,String la,String lb,String lc,String original){
        String[] p=original.split("\\|",-1); if(p.length!=3)return;
        EditText a=field(la,dec(p[0])), b=field(lb,dec(p[1])), c=field(lc,dec(p[2]));
        LinearLayout box=new LinearLayout(this);box.setOrientation(1);box.setPadding(dp(18),dp(10),dp(18),dp(10));
        box.addView(text("Edit "+heading,20,true,ink));box.addView(a);box.addView(b);box.addView(c);
        new AlertDialog.Builder(this).setView(box).setNegativeButton("Cancel",null).setPositiveButton("Save",(d,w)->{
            String row=enc(a.getText().toString())+"|"+enc(b.getText().toString())+"|"+enc(c.getText().toString());
            replaceRow(key,original,row);show(page);
        }).show();
    }
    void assignments(){
        content.addView(text("✅ Assignments",22,true,ink));
        EditText title=field("Assignment name",""), subject=field("Subject / course",""), due=field("Due date",""); LinearLayout f=card();
        f.addView(title);f.addView(subject);f.addView(due);
        f.addView(button("Save assignment",()->{
            if(title.getText().toString().trim().isEmpty()){toast("Enter assignment name");return;}
            String row="false|"+enc(title.getText().toString())+"|"+enc(subject.getText().toString())+"|"+enc(due.getText().toString());
            String old=prefs.getString("assignments","");prefs.edit().putString("assignments",old.isEmpty()?row:old+"\n"+row).apply();show("Assignments");
        }));content.addView(f);
        String all=prefs.getString("assignments","");if(all.isEmpty()){content.addView(text("No assignments yet.",14,false,muted));return;}
        for(String row:all.split("\n")){String[] p=row.split("\\|",-1);if(p.length!=4)continue;final String original=row;
            LinearLayout item=card();item.addView(text((p[0].equals("true")?"✓ Completed: ":"◯ Pending: ")+dec(p[1]),16,true,ink));
            item.addView(text(dec(p[2])+"  •  "+dec(p[3]),13,false,muted));LinearLayout actions=new LinearLayout(this);
            actions.addView(button(p[0].equals("true")?"Mark pending":"Complete",()->{
                String nr=(p[0].equals("true")?"false":"true")+"|"+p[1]+"|"+p[2]+"|"+p[3];replaceRow("assignments",original,nr);show("Assignments");}));
            actions.addView(button("Edit",()->editAssignment(original)));
            actions.addView(button("Delete",()->{removeRow("assignments",original);show("Assignments");}));item.addView(actions);content.addView(item);
        }
    }
    void editAssignment(String original){
        String[] p=original.split("\\|",-1);if(p.length!=4)return;
        EditText a=field("Assignment name",dec(p[1])),b=field("Subject",dec(p[2])),c=field("Due date",dec(p[3]));
        LinearLayout box=new LinearLayout(this);box.setOrientation(1);box.setPadding(dp(18),dp(10),dp(18),dp(10));box.addView(a);box.addView(b);box.addView(c);
        new AlertDialog.Builder(this).setTitle("Edit assignment").setView(box).setNegativeButton("Cancel",null).setPositiveButton("Save",(d,w)->{
            replaceRow("assignments",original,p[0]+"|"+enc(a.getText().toString())+"|"+enc(b.getText().toString())+"|"+enc(c.getText().toString()));show("Assignments");
        }).show();
    }
    String enc(String s){return s.replace("%","%25").replace("|","%7C").replace("\n"," ");}
    String dec(String s){return s.replace("%7C","|").replace("%25","%");}
    void removeRow(String key,String target){StringBuilder out=new StringBuilder();for(String r:prefs.getString(key,"").split("\n")){if(r.equals(target))continue;if(out.length()>0)out.append("\n");out.append(r);}prefs.edit().putString(key,out.toString()).apply();}
    void replaceRow(String key,String old,String now){StringBuilder out=new StringBuilder();boolean done=false;for(String r:prefs.getString(key,"").split("\n")){if(!done&&r.equals(old)){r=now;done=true;}if(out.length()>0)out.append("\n");out.append(r);}prefs.edit().putString(key,out.toString()).apply();}
    void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
}
