package com.netflixclone.data

import com.netflixclone.network.services.ApiClient



object GitRepository {

    suspend fun getGitData(s:String) = ApiClient.GITDB.getPlainText(s)

}