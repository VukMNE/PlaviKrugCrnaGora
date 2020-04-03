package me.plavikrug;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import me.plavikrug.db.DataBaseSource;

/**
 * Created by Vuk on 23.11.2017..
 */

public class SamoupravniWebProgram extends AsyncTask<String, Void, String[]> {

    public AsyncResponse delegate = null;
    private DataBaseSource dbSource;
    SharedPreferences podaci;
    SharedPreferences.Editor editor;

    @Override
    protected String[] doInBackground(String... params) {
        Socket s1 = null;
        String prijem = "";
        String[] help = null;

        try {
            s1 = new Socket("185.58.193.218",2702);
        } catch (IOException e) {
            e.printStackTrace();
        }
        OutputStream s1Out = null;
        try {
            s1Out = s1.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataOutputStream dos = new DataOutputStream(s1Out);
        try {
             if(params.length==3) {
                 dos.writeUTF(params[0]+"```"+params[1]+"```"+params[2]);
             }
            else if(params.length==2){
                 dos.writeUTF(params[0]+"```"+params[1]);
             }
            else{
                 dos.writeUTF(params[0]);
             }

        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream s1in = null;
        try {
            s1in = s1.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataInputStream dis = new DataInputStream(s1in);
        try {
            prijem = dis.readUTF();
            help = prijem.split("€€");
        } catch (IOException e) {
            e.printStackTrace();
        }



        try {
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            s1in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            s1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            s1Out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            s1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return help;
    }

    @Override
    protected void onPostExecute(String[] strings){
        try {
            delegate.processFinish(strings);
        }
        catch(JSONException js){
            js.printStackTrace();
        }
    }
}
