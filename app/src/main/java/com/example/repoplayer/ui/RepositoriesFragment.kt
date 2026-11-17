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

package com.example.repoplayer.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repoplayer.R
import com.example.repoplayer.data.model.RepoDefinition
import com.example.repoplayer.data.store.RepoEntry
import com.example.repoplayer.data.store.RepoPreferences

class RepositoriesFragment : Fragment() {

    private lateinit var repoPreferences: RepoPreferences
    private lateinit var inputBox: EditText
    private lateinit var addButton: Button
    private lateinit var sampleButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: TextView
    private lateinit var adapter: RepoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_repositories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repoPreferences = RepoPreferences(requireContext())
        inputBox = view.findViewById(R.id.repoInput)
        addButton = view.findViewById(R.id.addRepoButton)
        sampleButton = view.findViewById(R.id.demoRepoButton)
        recyclerView = view.findViewById(R.id.repoRecycler)
        emptyState = view.findViewById(R.id.repoEmptyState)

        adapter = RepoAdapter(repoPreferences.listRepos())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        addButton.setOnClickListener {
            val url = inputBox.text?.toString()?.trim().orEmpty()
            if (url.isBlank()) {
                Toast.makeText(requireContext(), R.string.error_invalid_repo, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                val repoDefinition = repoPreferences.validateUrl(url)
                val entry = RepoEntry(
                    id = repoDefinition.id,
                    name = repoDefinition.name,
                    url = url,
                    version = repoDefinition.version,
                    extensionCount = repoDefinition.extensions.size
                )
                repoPreferences.addRepo(entry)
                adapter.updateItems(repoPreferences.listRepos())
                Toast.makeText(requireContext(), R.string.info_repo_added, Toast.LENGTH_SHORT).show()
                inputBox.setText("")
            } catch (throwable: Throwable) {
                Toast.makeText(requireContext(), R.string.error_invalid_repo, Toast.LENGTH_SHORT).show()
            }
        }

        sampleButton.setOnClickListener {
            val entry = repoPreferences.loadSampleRepo()
            repoPreferences.addRepo(entry)
            adapter.updateItems(repoPreferences.listRepos())
            Toast.makeText(requireContext(), R.string.info_repo_added, Toast.LENGTH_SHORT).show()
        }

        recyclerView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                val position = recyclerView.getChildAdapterPosition(recyclerView.focusedChild)
                if (position != RecyclerView.NO_POSITION) {
                    val item = adapter.getItem(position)
                    Toast.makeText(requireContext(), item.name, Toast.LENGTH_SHORT).show()
                    return@setOnKeyListener true
                }
            }
            false
        }

        updateEmptyState()
    }

    private fun updateEmptyState() {
        val items = repoPreferences.listRepos()
        emptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
    }

    class RepoAdapter(private var items: List<RepoEntry>) : RecyclerView.Adapter<RepoAdapter.RepoViewHolder>() {

        fun updateItems(newItems: List<RepoEntry>) {
            items = newItems
            notifyDataSetChanged()
        }

        fun getItem(position: Int): RepoEntry = items[position]

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_repo, parent, false)
            return RepoViewHolder(view)
        }

        override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
            val item = items[position]
            holder.nameView.text = item.name
            holder.urlView.text = item.url
            holder.removeButton.setOnClickListener {
                val prefs = RepoPreferences(holder.itemView.context)
                prefs.removeRepo(item.url)
                updateItems(prefs.listRepos())
            }
        }

        override fun getItemCount(): Int = items.size

        class RepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameView: TextView = itemView.findViewById(R.id.repoName)
            val urlView: TextView = itemView.findViewById(R.id.repoUrl)
            val removeButton: Button = itemView.findViewById(R.id.removeRepoButton)
        }
    }
}
