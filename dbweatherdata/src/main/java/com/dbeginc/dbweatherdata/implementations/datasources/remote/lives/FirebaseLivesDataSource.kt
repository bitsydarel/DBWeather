/*
 *  Copyright (C) 2017 Darel Bitsy
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.dbeginc.dbweatherdata.implementations.datasources.remote.lives

import android.content.Context
import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdata.IPTV_LIVES_FIREBASE_REFERENCE
import com.dbeginc.dbweatherdata.YOUTUBE_LIVES_FIREBASE_REFERENCE
import com.dbeginc.dbweatherdata.implementations.datasources.remote.RemoteLivesDataSource
import com.dbeginc.dbweatherdata.proxies.mappers.toDomain
import com.dbeginc.dbweatherdata.proxies.remote.lives.RemoteIpTvPlaylist
import com.dbeginc.dbweatherdata.proxies.remote.lives.RemoteYoutubeLive
import com.dbeginc.dbweatherdomain.entities.lives.IpTvPlaylist
import com.dbeginc.dbweatherdomain.entities.lives.YoutubeLive
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.reactivex.Observable
import io.reactivex.Single
import java.io.File

@RestrictTo(RestrictTo.Scope.LIBRARY)
class FirebaseLivesDataSource private constructor(
        private val youtubeLivesTable: DatabaseReference,
        private val iptvPlaylistTable: DatabaseReference,
        private val ipTvFiles: StorageReference,
        private val ipTvFileParser: IpTvFileParser
) : RemoteLivesDataSource {

    companion object {
        @JvmStatic
        fun create(context: Context): FirebaseLivesDataSource {

            val app: FirebaseApp = FirebaseApp.getInstance() ?: FirebaseApp.initializeApp(context)!!

            val firebaseDatabase = FirebaseDatabase
                    .getInstance(app)
                    .reference

            val youtubeTableRef = firebaseDatabase
                    .child(YOUTUBE_LIVES_FIREBASE_REFERENCE)

            val iptvPlaylistTable = firebaseDatabase
                    .child(IPTV_LIVES_FIREBASE_REFERENCE)

            val firebaseStorage = FirebaseStorage.getInstance(app)

            firebaseStorage.maxUploadRetryTimeMillis = 300000

            return FirebaseLivesDataSource(
                    youtubeLivesTable = youtubeTableRef,
                    ipTvFiles = firebaseStorage.reference,
                    iptvPlaylistTable = iptvPlaylistTable,
                    ipTvFileParser = M3UFileParser()
            )
        }
    }

    override fun getAllYoutubeLives(): Single<List<YoutubeLive>> {
        return Single.create<List<RemoteYoutubeLive>> { emitter ->
            youtubeLivesTable.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    return emitter.onError(databaseError.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val lives = mutableListOf<RemoteYoutubeLive>()

                    dataSnapshot.children.mapTo(lives) { snapshot ->
                        RemoteYoutubeLive(snapshot.key, snapshot.value as? String ?: "")
                    }

                    emitter.onSuccess(lives)
                }
            })
        }.map { lives -> lives.map { live -> live.toDomain() } }
    }

    override fun getYoutubeLive(name: String): Single<YoutubeLive> {
        return Single.create<RemoteYoutubeLive> { emitter ->
            youtubeLivesTable.child(name)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(databaseError: DatabaseError) = emitter.onError(databaseError.toException())

                        override fun onDataChange(data: DataSnapshot) {
                            emitter.onSuccess(RemoteYoutubeLive(name = name, url = data.getValue(String::class.java)
                                    ?: ""))
                        }
                    })
        }.map { it.toDomain() }
    }

    override fun getAllIpTvPlaylist(): Single<List<IpTvPlaylist>> {
        val getPlaylistInfo: Single<List<Pair<String, String>>> = Single.create { emitter ->
            iptvPlaylistTable
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(databaseError: DatabaseError) =
                                emitter.onError(databaseError.toException())

                        override fun onDataChange(value: DataSnapshot) =
                                emitter.onSuccess(value.children.map { it.key to it.value as String })
                    })
        }

        val getPlaylistFile: (String, String) -> Single<Pair<String, File>> = { playlistId, filePath ->
            Single.create { emitter ->

                val tempFile = File.createTempFile(filePath.substringBeforeLast(delimiter = "."), filePath.substringAfterLast(delimiter = "."))

                ipTvFiles.child(filePath)
                        .getFile(tempFile)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) emitter.onSuccess(playlistId to tempFile) else emitter.onError(task.exception!!)
                        }
            }
        }

        val parsePlaylist: (String, File) -> Single<RemoteIpTvPlaylist> = { playlistId, iptvFile ->
            Single.fromCallable { ipTvFileParser.parseFile(defaultName = playlistId, file = iptvFile) }
        }

        return getPlaylistInfo.flatMapObservable { nameWithFilePath -> Observable.fromIterable(nameWithFilePath) }
                .flatMapSingle { (name, filePath) -> getPlaylistFile(name, filePath) }
                .flatMapSingle { (name, file) -> parsePlaylist(name, file) }
                .collectInto(mutableListOf<RemoteIpTvPlaylist>()) { listOfPlaylist, playlist -> listOfPlaylist.add(playlist) }
                .map { listOfPlaylist -> listOfPlaylist.map { it.toDomain() } }
    }
}