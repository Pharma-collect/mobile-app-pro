package fr.isen.emelian.pharma_collect_pro.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import fr.isen.emelian.pharma_collect_pro.dataClass.User
import org.json.JSONObject
import java.io.File

class FileService {

    /*
     * INFORMATION : To find file : "Device File Explorer" --> data/data/fr.isen.emelian ../cacheData_user.json
     */

    var user: User = User()

    /*
     * Write a cache file with user informations.
     */
    fun saveData(id: String, username: String, pharmaId: String, token: String, context: Context){
        if(id.isNotEmpty() && username.isNotEmpty() && pharmaId.isNotEmpty() && token.isNotEmpty()){
            val donnees = "{'id': '$id', 'username': '$username', 'pharmaId': '$pharmaId', 'token': '$token'}"
            File(context.cacheDir.absolutePath + "Data_user.json").writeText(donnees)
            Toast.makeText(context, "Welcome into Pharma-collect", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(context, "Login error", Toast.LENGTH_LONG).show()
        }
    }

    /*
     * Read a cache file with user informations.
     */
    fun getData(context: Context): User {
        val datas: String = File(context.cacheDir.absolutePath + "Data_user.json").readText()
        if (datas.isNotEmpty()) {
            val jsonObject = JSONObject(datas)
            user.id = jsonObject.optString("id")
            user.token = jsonObject.optString("token")
            user.username = jsonObject.optString("username")
            user.pharma_id = jsonObject.optString("pharmaId")
        }
        return user
    }

    /*
     * Delete a cache file when deconnection.
     */
    fun deleteData(context: Context): Boolean {
        val file = File(context.cacheDir.absolutePath + "Data_user.json")
        val response = file.deleteRecursively()
        return response
    }

}