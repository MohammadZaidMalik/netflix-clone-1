package com.netflixclone.data

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.netflixclone.db.MovieDb
import com.netflixclone.db.entity.MovieEntity
import com.netflixclone.db.entity.MovieSyncLogEntity
import com.netflixclone.db.repos.MovieEntityDao
import com.netflixclone.network.models.MovieDetailsResponse
import com.netflixclone.network.services.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Scanner
import java.util.concurrent.Executor
import java.util.concurrent.Executors


object GitRepository
{
    suspend fun syncGitData()
    {
//        val scope = CoroutineScope(Dispatchers.IO) // Use IO dispatcher for database operations
//        scope.launch {
//            syncMovieData();
//        }

        var ex:Executor = Executors.newSingleThreadExecutor();
        ex.execute({->
            syncMovieData();
    });
//        ex.execute({
//            ->syncSeriesData()
//        });

    }

    private fun syncMovieData()
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
                        parseMovieMaster(posts);
                    }
                    else
                    {
                        Log.e("GitRepository", "Error: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("GitRepository", "Network call failed: ${t.message}")
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
                        parseSeriesMaster(posts);
                    }
                    else
                    {
                        Log.e("GitRepository", "Error: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("GitRepository", "Network call failed: ${t.message}")
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

    private fun parseMovieMaster(movieMaster:String?)
    {
        val sc = Scanner(movieMaster);
        val dbFiles = ArrayList<String?>();

        while(sc.hasNext())
            dbFiles.add(sc.nextLine());
        sc.close();
        loadMoviesFromDbFiles(dbFiles);
    }

    private fun loadSeriesFromDbFiles(dbFiles: ArrayList<String?>)
    {
        dbFiles.forEach{dbFileName ->
            if(dbFileName == null || dbFileName.trim().isEmpty())
                return;

            val call:Call<String> = getGitData(dbFileName)
            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>)
                {
                    if (response.isSuccessful)
                    {
                        val seriesData: String? = response.body()
                        processSeriesDBData(seriesData);
                    }
                    else
                    {
                        Log.e("GitRepository", "Error: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("GitRepository", "Network call failed: ${t.message}")
                }
            })

        }
    }

    private fun loadMoviesFromDbFiles(dbFiles: ArrayList<String?>)
    {
        var lastSyncE:MovieSyncLogEntity? = MovieDb.getMovieDB().movieLastSyncDao().getLastSync();
        var lastSyncAt:Long = 0;

        val movieDB: MovieEntityDao = MovieDb.getMovieDB().movieEntityDao()

        if(lastSyncE != null)
            lastSyncAt = lastSyncE.lastSyncedAt;


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
//                        Log.i("GitRepository","Git movie db data $posts");
                        processMovieDBData(posts,movieDB,lastSyncAt);
                    }
                    else
                    {
                        Log.e("GitRepository", "Error: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("GitRepository", "Network call failed: ${t.message}")
                }
            })

        }

        if(lastSyncE == null) {
            lastSyncE = MovieSyncLogEntity(1, System.currentTimeMillis());
            MovieDb.getMovieDB().movieLastSyncDao().insertAll(lastSyncE);
        }
        else
        {
            lastSyncE.lastSyncedAt = System.currentTimeMillis();
            MovieDb.getMovieDB().movieLastSyncDao().update(lastSyncE);
        }


    }

    private fun processMovieDBData(data: String?,movieDB: MovieEntityDao, lastSyncAt: Long)
    {
//        Log.i("GitRepository","Git movie db files data : $data");
        if(data == null || data.trim().isEmpty())
            return ;

//        var movieDataToApply:MutableList<String> =  ArrayList<String>()
        val sc :Scanner =  Scanner(data);
        while(sc.hasNext())
        {
            val line:String = sc.nextLine();
            val movieDataParts:List<String> = line.split("\\|".toRegex());
//            movieDataParts.forEach({ movieDataPart->
//                Log.i("GitRepository","Git movie db data $movieDataPart");
//
//            })
            try {
                val timestamp:Long = movieDataParts[0].toLong();
                if(timestamp > lastSyncAt)
                    applyToMovieDB(movieDataParts,movieDB);
            }
            catch(e:Exception)
            {
                e.printStackTrace();
            }

        }
        sc.close();
//        saveMovieDataToDB(movieDataMap,movieDB);
    }

    private fun applyToMovieDB(movieDataParts:List<String>,movieDB: MovieEntityDao)
    {

//        Log.i("GitRepository","Applying $movieDataParts")
        var index:Int = 0;
        val timestamp:Long = movieDataParts[index++].toLong();
        val type:String = movieDataParts[index++];
        val id:String = movieDataParts[index++];
        val tmdbId = movieDataParts[index++].toInt();
        val gitRepo = movieDataParts[index];

        if(type == "A")
            addMovieToDB(id,tmdbId,gitRepo,movieDB,timestamp);
        else if(type == "U")
            updateMovieToDB(id,tmdbId,gitRepo,movieDB,timestamp);
        else if(type == "D")
            deleteMovie(id,movieDB);
    }

    private fun deleteMovie(id: String,movieDB: MovieEntityDao)
    {
        val movieEntity:MovieEntity? = movieDB.findById(id);
        if(movieEntity != null)
            movieDB.delete(movieEntity);
    }

    private fun updateMovieToDB(id:String, tmdbId:Int, gitRepo:String,movieDB: MovieEntityDao, timestamp:Long)
    {
        val movieEntity:MovieEntity? = movieDB.findById(id);
        if(movieEntity == null) {
            addMovieToDB(id, tmdbId, gitRepo, movieDB, timestamp);
            return;
        }
        val call:Call<MovieDetailsResponse> = ApiClient.TMDB.fetchMovieById(tmdbId);
        call.enqueue(object : Callback<MovieDetailsResponse> {
            override fun onResponse(call: Call<MovieDetailsResponse>, response: Response<MovieDetailsResponse>)
            {
                if (response.isSuccessful) {
                    val movieTmdbData: MovieDetailsResponse? = response.body()

                    var tmdbId = movieTmdbData?.id;
                    var title = movieTmdbData?.title;
                    var overview = movieTmdbData?.overview;

                    if(title == null)
                        title = "";

                    if(overview == null)
                        overview = "";

                    if(tmdbId == null)
                        tmdbId = 0;



                        movieEntity.id = tmdbId
                        movieEntity.title = title;
                        movieEntity.posterPath = movieTmdbData?.posterPath;
                        movieEntity.backdropPath = movieTmdbData?.backdropPath;
                        movieEntity.overview = overview
                        movieEntity.releaseDate = movieTmdbData?.releaseDate;
                        movieEntity.voteAverage = movieTmdbData?.voteAverage;
                        movieEntity.genreIds = movieTmdbData?.genreIds
                        movieEntity.streamingLink = gitRepo
                        movieEntity.timestamp = timestamp
                        movieDB.update(movieEntity);
                }
                else
                    Log.e("GitRepository", "Error: ${response.code()} ${response.message()}")
            }

            override fun onFailure(call: Call<MovieDetailsResponse>, t: Throwable) {
                Log.e("GitRepository", "Network call failed: ${t.message}",t)
            }
        })
    }
    private fun addMovieToDB(id:String, tmdbId:Int, gitRepo:String,movieDB: MovieEntityDao, timestamp:Long)
    {

        val movieE:MovieEntity? = movieDB.findById(id);
        if(movieE != null) {
            updateMovieToDB(id, tmdbId, gitRepo, movieDB, timestamp);
            return;
        }
        val call:Call<MovieDetailsResponse> = ApiClient.TMDB.fetchMovieById(tmdbId);
        call.enqueue(object : Callback<MovieDetailsResponse> {
            override fun onResponse(call: Call<MovieDetailsResponse>, response: Response<MovieDetailsResponse>)
            {
                if (response.isSuccessful) {
                    val movieTmdbData: MovieDetailsResponse? = response.body()
                    var tmdbId = movieTmdbData?.id;
                    var title = movieTmdbData?.title;
                    var overview = movieTmdbData?.overview;

                    if(title == null)
                        title = "";

                    if(overview == null)
                        overview = "";
                    if(tmdbId == null)
                        tmdbId = 0;
                    val movieEntity:MovieEntity = MovieEntity(
                        tmdbId,
                        id,
                        title,
                        movieTmdbData?.posterPath,
                        movieTmdbData?.backdropPath,
                        overview,
                        movieTmdbData?.releaseDate,
                        movieTmdbData?.voteAverage,
                        movieTmdbData?.genreIds,
                        gitRepo,
                        timestamp
                    );
                    try {
                        movieDB.insertAll(movieEntity);

                    }
                    catch (e:SQLiteConstraintException)
                    {
                        updateMovieToDB(id, tmdbId, gitRepo, movieDB, timestamp);
                    }
//                    Log.i("GitRepository","added : " + movieEntity)
                }
                else
                    Log.e("GitRepository", "Error: ${response.code()} ${response.message()}")
            }

            override fun onFailure(call: Call<MovieDetailsResponse>, t: Throwable) {
                Log.e("GitRepository", "Network call failed: ${t.message}")
            }
        })
    }

    private fun processSeriesDBData(data: String?)
    {
        if(data == null || data.trim().isEmpty())
            return ;

        val sc :Scanner =  Scanner(data);
        while(sc.hasNext())
        {
            val line:String = sc.nextLine();
            val seriesDataParts:List<String> = line.split("\\|");
            try
            {
                val timestamp:Long = seriesDataParts[0].toLong();
//                if(timestamp > lastSyncAt)
//                    applyToMovieDB(movieDataParts,movieDB);
            }
            catch(e:Exception)
            {
                e.printStackTrace();
            }
        }
        sc.close();
    }

    private fun getGitData(s:String) = ApiClient.GITDB.getPlainText(s)

}