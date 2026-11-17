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

package com.example.repoplayer.data.model

import org.json.JSONArray
import org.json.JSONObject

private const val EMPTY = ""

data class RepoDefinition(
    val id: String,
    val name: String,
    val description: String = EMPTY,
    val version: String = EMPTY,
    val minAppVersion: String = EMPTY,
    val language: String = "en",
    val supportedContentTypes: List<String> = emptyList(),
    val iconUrl: String = EMPTY,
    val status: String = "ok",
    val authors: List<String> = emptyList(),
    val extensions: List<ExtensionDefinition> = emptyList()
)

data class ExtensionDefinition(
    val id: String,
    val name: String,
    val description: String = EMPTY,
    val version: String = EMPTY,
    val language: String = "en",
    val minAppVersion: String = EMPTY,
    val supportedContentTypes: List<String> = emptyList(),
    val iconUrl: String = EMPTY,
    val downloadUrl: String = EMPTY,
    val sha256: String = EMPTY,
    val authors: List<String> = emptyList(),
    val status: String = "ok",
    val nsfw: Boolean = false
)

object RepoJsonParser {

    fun parse(rawJson: String): RepoDefinition {
        val root = JSONObject(rawJson)

        val extensionArray = root.optJSONArray("extensions") ?: JSONArray()
        val extensions = mutableListOf<ExtensionDefinition>()
        for (index in 0 until extensionArray.length()) {
            val extensionObject = extensionArray.getJSONObject(index)
            extensions += ExtensionDefinition(
                id = extensionObject.optString("id", ""),
                name = extensionObject.optString("name", ""),
                description = extensionObject.optString("description", EMPTY),
                version = extensionObject.optString("version", EMPTY),
                language = extensionObject.optString("language", "en"),
                minAppVersion = extensionObject.optString("minAppVersion", EMPTY),
                supportedContentTypes = readStringList(extensionObject.optJSONArray("supportedContentTypes")),
                iconUrl = extensionObject.optString("icon", EMPTY),
                downloadUrl = extensionObject.optString("downloadUrl", EMPTY),
                sha256 = extensionObject.optString("sha256", EMPTY),
                authors = readStringList(extensionObject.optJSONArray("authors")),
                status = extensionObject.optString("status", "ok"),
                nsfw = extensionObject.optBoolean("nsfw", false)
            )
        }

        return RepoDefinition(
            id = root.optString("id", "repo"),
            name = root.optString("name", "Untitled Repo"),
            description = root.optString("description", EMPTY),
            version = root.optString("version", EMPTY),
            minAppVersion = root.optString("minAppVersion", EMPTY),
            language = root.optString("language", "en"),
            supportedContentTypes = readStringList(root.optJSONArray("supportedContentTypes")),
            iconUrl = root.optString("icon", EMPTY),
            status = root.optString("status", "ok"),
            authors = readStringList(root.optJSONArray("authors")),
            extensions = extensions
        )
    }

    private fun readStringList(array: JSONArray?): List<String> {
        if (array == null) return emptyList()
        return List(array.length()) { index -> array.getString(index) }
    }
}
