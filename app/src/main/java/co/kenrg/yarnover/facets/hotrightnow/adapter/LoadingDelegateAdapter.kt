package co.kenrg.yarnover.facets.hotrightnow.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.kenrg.yarnover.R
import co.kenrg.yarnover.iface.adapter.DelegateAdapter

class LoadingDelegateAdapter : DelegateAdapter<ViewItem> {
  class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

  override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    return LoadingViewHolder(layoutInflater.inflate(R.layout.component_loading, parent, false))
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewItem) {}
}