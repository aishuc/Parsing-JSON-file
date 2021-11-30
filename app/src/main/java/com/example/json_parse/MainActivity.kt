package com.example.json_parse

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileInputStream
import java.lang.reflect.Parameter
import java.nio.channels.FileChannel
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    val REQUEST_READ_EXTERNAL = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        var text_view : TextView = findViewById(R.id.text_view)

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
        !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_READ_EXTERNAL)
        }else{
            parseJson()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_READ_EXTERNAL) parseJson()
    }

    private fun parseJson() {
       val jsonObject = JSONObject(readFile())

        val config = getConfig(jsonObject.getJSONObject("config"))

        val parameters = getParameters(jsonObject.getJSONArray("parameters"))

        var string = "${config.Company} is the company .${config.FP_version} is the FP version\n " +
                "Configuration as ${config.Family}:Family\n" +
                "${config.CAN_ID}: CAN ID" +
                "${config.DT_UTC}: DT UTC" +
                "${config.user}: user" +
                "${config.unit}: unit" +
                "${config.DD_localisation}: DD localisation"+"parameters are"
         parameters.forEach {
             string += "${it.id}: id ,${it.restore}: restore ,${it.value}: value,\n"
         }

        val text_view : TextView = findViewById(R.id.text_view)
        text_view.text = string

    }

    private fun getParameters(jsonArray: JSONArray): ArrayList<com.example.json_parse.Parameter> {
        var parameters = ArrayList<com.example.json_parse.Parameter>()
         var x = 0
         while (x < jsonArray.length()){
           parameters.add(
            com.example.json_parse.Parameter(
                jsonArray.getJSONArray(x).getString(0),
                jsonArray.getJSONArray(x).getInt(1),
                jsonArray.getJSONArray(x).getInt(2)

           ))
             x++
         }
        return parameters
    }

    private fun getConfig(jsonObject: JSONObject): Config {
        return Config(
            jsonObject.getString("Company"),
            jsonObject.getString("FP_version"),
            jsonObject.getString("Family"),
            jsonObject.getString("CAN_ID"),
            jsonObject.getString("DT_UTC"),
            jsonObject.getInt("user"),
            jsonObject.getInt("unit"),
            jsonObject.getString("DD_localisation")
        )
    }

    private fun readFile(): String {



                val file =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .absolutePath + "/myJson/myJson1703947195589752478.json"

                val stream = FileInputStream(file)
                var jsonString = ""
                stream.use { stream ->
                    val fileChannel = stream.channel
                    val mappedByteBuffer = fileChannel.map(
                        FileChannel.MapMode.READ_ONLY,
                        0,
                        fileChannel.size()
                    )
                    jsonString = Charset.defaultCharset().decode(mappedByteBuffer).toString()
                }
                return jsonString

        }
}