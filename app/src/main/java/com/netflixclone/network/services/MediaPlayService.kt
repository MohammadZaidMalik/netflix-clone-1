package com.netflixclone.network.services

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri

object MediaPlayService
{
     fun play(link: String?, title:String, context: Context?)
    {
        // Create an AlertDialog
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Play with")
        builder.setPositiveButton("VLC") { dialog, which ->
            playWithVlc(link,title,context);
        }
        builder.setNegativeButton("Mx Player") { dialog, which ->
            playWithMx(link,title,context);
        }

        // Show the dialog
        builder.show()
    }

    private fun playWithMx(link:String?,title:String,context:Context?)
    {
        playWith("com.mxtech.videoplayer.ad",link,title,context);
    }

    private fun playWith(packageName: String, link: String?, title: String, context: Context?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse("https://raw.githubusercontent.com/faltu-giri/"+link+"/refs/heads/master/out.m3u8#"+title), "video/*")
        intent.putExtra("title", title);
        intent.setPackage(packageName);
        context?.startActivity(intent)
    }

    private fun playWithVlc(link:String?,title:String,context:Context?)
    {
        playWith("org.videolan.vlc",link,title,context)
    }
}