package co.kenrg.yarnover.facets.hotrightnow.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

interface ViewTypeDelegateAdapter {
  fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
  fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType)
}
