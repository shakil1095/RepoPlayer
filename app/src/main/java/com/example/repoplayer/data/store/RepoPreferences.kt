/*
 * RepoPlayer - A privacy-respecting media center app for Android.
 * Copyright (C) 2026
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.example.repoplayer.data.store

import android.content.Context
import androidx.core.content.edit
import com.example.repoplayer.data.model.RepoDefinition
import com.example.repoplayer.data.model.RepoJsonParser
import org.json.JSONArray
import org.json.JSONObject

private const val PREFS_NAME = "repo_player_prefs"
private const val KEY_REPOS = "saved_repos"
private const val KEY_THEME = "theme_mode"

data class RepoEntry(
    val id: String,
    val name: String,
    val url: String,
    val version: String = "",
    val extensionCount: Int = 0
)

class RepoPreferences(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setDarkMode(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_THEME, enabled) }
    }

    fun isDarkModeEnabled(): Boolean = prefs.getBoolean(KEY_THEME, true)

    fun listRepos(): List<RepoEntry> {
        val raw = prefs.getString(KEY_REPOS, "[]") ?: "[]"
        val array = JSONArray(raw)
        val list = mutableListOf<RepoEntry>()
        for (index in 0 until array.length()) {
            val obj = array.getJSONObject(index)
            list += RepoEntry(
                id = obj.optString("id", ""),
                name = obj.optString("name", "Untitled Repo"),
                url = obj.optString("url", ""),
                version = obj.optString("version", ""),
                extensionCount = obj.optInt("extensionCount", 0)
            )
        }
        return list
    }

    fun addRepo(entry: RepoEntry) {
        val repos = listRepos().toMutableList()
        if (repos.none { it.url == entry.url }) {
            repos += entry
            persist(repos)
        }
    }

    fun removeRepo(url: String) {
        val repos = listRepos().filterNot { it.url == url }.toMutableList()
        persist(repos)
    }

    fun clearRepos() {
        prefs.edit { remove(KEY_REPOS) }
    }

    fun loadSampleRepo(): RepoEntry {
        val repo = RepoJsonParser.parse(
            readAssetText("sample_repo.json")
        )
        return RepoEntry(
            id = repo.id,
            name = repo.name,
            url = "assets://sample_repo.json",
            version = repo.version,
            extensionCount = repo.extensions.size
        )
    }

    fun validateUrl(rawUrl: String): RepoDefinition {
        val cleanUrl = rawUrl.trim()
        val decoded = if (cleanUrl.startsWith("repoplayer://add?url=")) {
            parseCustomScheme(cleanUrl)
        } else {
            cleanUrl
        }

        val json = if (decoded.startsWith("assets://")) {
            readAssetText("sample_repo.json")
        } else {
            val request = okhttp3.Request.Builder().url(decoded).build()
            val response = okhttp3.OkHttpClient().newCall(request).execute()
            if (!response.isSuccessful) {
                throw IllegalArgumentException("HTTP ${response.code}")
            }
            response.body?.string() ?: throw IllegalStateException("Empty body")
        }

        return RepoJsonParser.parse(json)
    }

    private fun persist(repos: List<RepoEntry>) {
        val array = JSONArray()
        repos.forEach { repo ->
            val obj = JSONObject()
            obj.put("id", repo.id)
            obj.put("name", repo.name)
            obj.put("url", repo.url)
            obj.put("version", repo.version)
            obj.put("extensionCount", repo.extensionCount)
            array.put(obj)
        }
        prefs.edit { putString(KEY_REPOS, array.toString()) }
    }

    private fun parseCustomScheme(rawUrl: String): String {
        return java.net.URLDecoder.decode(
            rawUrl.removePrefix("repoplayer://add?url="),
            "UTF-8"
        )
    }

    private fun readAssetText(assetName: String): String {
        val stream = context.assets.open(assetName)
        return stream.bufferedReader().use { it.readText() }
    }
}
