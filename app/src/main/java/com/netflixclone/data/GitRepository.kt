package com.netflixclone.data

import android.content.Context
import android.util.Log
import com.netflixclone.db.MovieDb
import com.netflixclone.network.services.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap
import java.util.Scanner


object GitRepository
{
    suspend fun syncGitData(context:Context)
    {
        syncMovieData(context);
        syncSeriesData()
    }

    private fun syncMovieData(context:Context)
    {
        try {

            val call:Call<String> = getGitData("m_master.txt")
            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>)
                {
                    if (response.isSuccessful)
                    {
                        val posts: String? = response.body()
                        Log.i("GitRepository","Git movie master data $posts");
                        parseMovieMaster(posts,context);
                    }
                    else
                    {
                        Log.e("RetrofitExample", "Error: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("RetrofitExample", "Network call failed: ${t.message}")
                }
            })

        } catch (ex: Exception) {
            ex.printStackTrace();
        }
    }

    private fun syncSeriesData()
    {
        try {

            val call:Call<String> = getGitData("s_master.txt")
            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>)
                {
                    if (response.isSuccessful)
                    {
                        val posts: String? = response.body()
                        Log.i("GitRepository","Git movie master data $posts");
                        parseSeriesMaster(posts);
                    }
                    else
                    {
                        Log.e("RetrofitExample", "Error: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("RetrofitExample", "Network call failed: ${t.message}")
                }
            })

        } catch (ex: Exception) {
            ex.printStackTrace();
        }
    }

    private fun parseSeriesMaster(seriesMaster:String?)
    {
        val sc = Scanner(seriesMaster);
        val dbFiles = ArrayList<String?>();

        while(sc.hasNext())
            dbFiles.add(sc.nextLine());
        sc.close();
        loadSeriesFromDbFiles(dbFiles);
    }

    private fun parseMovieMaster(movieMaster:String?,context:Context)
    {
        val sc = Scanner(movieMaster);
        val dbFiles = ArrayList<String?>();

        while(sc.hasNext())
            dbFiles.add(sc.nextLine());
        sc.close();
        loadMoviesFromDbFiles(dbFiles,context);
    }

    private fun loadSeriesFromDbFiles(dbFiles: ArrayList<String?>)
    {
        dbFiles.forEach{dbFileName ->
            Log.i("GitRepository","Git series db files $dbFileName");
            if(dbFileName == null || dbFileName.trim().isEmpty())
                return;

            val call:Call<String> = getGitData(dbFileName)
            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>)
                {
                    if (response.isSuccessful)
                    {
                        val seriesData: String? = response.body()
                        Log.i("GitRepository","Git series db data $seriesData");
                        processSeriesDBData(seriesData);
                    }
                    else
                    {
                        Log.e("RetrofitExample", "Error: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("RetrofitExample", "Network call failed: ${t.message}")
                }
            })

        }
    }

    private fun loadMoviesFromDbFiles(dbFiles: ArrayList<String?>,context:Context)
    {
        dbFiles.forEach{dbFileName ->
            Log.i("GitRepository","Git movie db files $dbFileName");
                if(dbFileName == null || dbFileName.trim().isEmpty())
                    return;

                val call:Call<String> = getGitData(dbFileName)
            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>)
                {
                    if (response.isSuccessful)
                    {
                        val posts: String? = response.body()
                        Log.i("GitRepository","Git movie db data $posts");
                        processMovieDBData(posts,context);
                    }
                    else
                    {
                        Log.e("RetrofitExample", "Error: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("RetrofitExample", "Network call failed: ${t.message}")
                }
            })

        }
    }

    private fun processMovieDBData(data: String?,context:Context)
    {
        Log.i("GitRepository","Git movie db files data : $data");
        if(data == null || data.trim().isEmpty())
            return ;
        val movieDataMap:HashMap<String,String> = HashMap();

        val sc :Scanner =  Scanner(data);
        while(sc.hasNext())
        {
            val line:String = sc.nextLine();
            val movieDataParts:List<String> = line.split("\\|");
            movieDataParts.forEach({ movieDataPart->
                Log.i("GitRepository","Git movie db data $movieDataPart");
            })
            movieDataMap.put(movieDataParts.get(0),movieDataParts.get(1))
        }
        sc.close();
        saveMovieDataToDB(movieDataMap,context);
    }

    private fun saveMovieDataToDB(movieDataMap: HashMap<String, String>,context:Context)
    {
        MovieDb.getMovieDB(context);
    }

    private fun processSeriesDBData(data: String?)
    {
        Log.i("GitRepository","Git series db files data : $data");
        if(data == null || data.trim().isEmpty())
            return ;

        val sc :Scanner =  Scanner(data);
        while(sc.hasNext())
        {
            val line:String = sc.nextLine();
            val seriesDataParts:List<String> = line.split("\\|");
            seriesDataParts.forEach({ seriesDataPart-> Log.i("GitRepository","Git series db data $seriesDataPart"); })
        }
    }

    private fun getGitData(s:String) = ApiClient.GITDB.getPlainText(s)

}