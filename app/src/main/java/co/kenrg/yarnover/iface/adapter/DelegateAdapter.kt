package co.kenrg.yarnover.iface.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

interface DelegateAdapter<in T> {
  fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
  fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: T)
}