package com.example.diba.applicationparentchild;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class JSONParser {

    public JSONParser()
    {
        super();
    }

    public ArrayList<ParentTable> parseParent(JSONObject object)
    {
        ArrayList<ParentTable> arrayList = new ArrayList<ParentTable>();
        try {
            JSONArray jsonArray = object.getJSONArray("Value");
            JSONObject jsonObj = null;
            for(int i=0;i<jsonArray.length();i++)
            {
                jsonObj=jsonArray.getJSONObject(i);
                arrayList.add(new ParentTable(jsonObj.getString("parent_ip"), jsonObj.getString("email"), jsonObj.getString("password")));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d("JSONParser => parseParent", e.getMessage());
        }
        return arrayList;
    }

    public ArrayList<ChildTable> parseChild(JSONObject object)
    {
        ArrayList<ChildTable> arrayList = new ArrayList<ChildTable>();
        try {
            JSONArray jsonArray = object.getJSONArray("Value");
            JSONObject jsonObj = null;
            for(int i=0;i<jsonArray.length();i++)
            {
                jsonObj = jsonArray.getJSONObject(i);
                arrayList.add(new ChildTable(jsonObj.getString("child_ip"), jsonObj.getString("email"), jsonObj.getString("password"),
                                             jsonObj.getString("child_name"), jsonObj.getInt("age"), jsonObj.getString("problem"),
                                             jsonObj.getDouble("current_lat"), jsonObj.getDouble("current_lon"), jsonObj.getString("on_off")));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d("JSONParser => parseChild", e.getMessage());
        }
        return arrayList;
    }

    public ArrayList<RecordTable> parseRecord(JSONObject object)
    {
        ArrayList<RecordTable> arrayList = new ArrayList<RecordTable>();
        try {
            JSONArray jsonArray = object.getJSONArray("Value");
            JSONObject jsonObj = null;
            for(int i=0;i<jsonArray.length();i++)
            {
                jsonObj = jsonArray.getJSONObject(i);
                arrayList.add(new RecordTable(jsonObj.getString("record_date"), jsonObj.getString("email"), jsonObj.getString("child_name"),
                        jsonObj.getString("location_name"),jsonObj.getDouble("lat"), jsonObj.getDouble("lon")));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d("JSONParser => parseRecord", e.getMessage());
        }
        return arrayList;
    }

    public int parseCreateAuth(JSONObject object)
    {   int createAtuh = 0;
        try {
            createAtuh = object.getInt("Value");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d("JSONParser => parseCreateAuth", e.getMessage());
        }
        return createAtuh;
    }

    public String parseUserAuth(JSONObject object)
    {   String userAtuh = null;
        try {
            userAtuh= object.getString("Value");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d("JSONParser => parseUserAuth", e.getMessage());
        }
        return userAtuh;
    }
}

