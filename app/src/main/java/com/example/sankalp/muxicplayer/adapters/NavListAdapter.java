package com.example.sankalp.muxicplayer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.data.NavigationInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by sankalp on 9/21/2016.
 */
public class NavListAdapter extends RecyclerView.Adapter<NavListAdapter.NavViewHolder> {
    private LayoutInflater inflater;
    private ClickListener clickListener;
    List<NavigationInfo> navigationInfoList= Collections.emptyList();
    public NavListAdapter(Context context,List<NavigationInfo> navigationInfoList) {
        inflater=LayoutInflater.from(context);
        this.navigationInfoList=navigationInfoList;
    }
    @Override
    public NavViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.nav_list_item,parent,false);
        NavViewHolder viewHolder=new NavViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NavViewHolder holder, int position) {
        NavigationInfo currentInfo=navigationInfoList.get(position);
        holder.title.setText(currentInfo.navTilte);
        holder.icon.setImageResource(currentInfo.navIconId);

    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener=clickListener;
    }

    @Override
    public int getItemCount()
    {
        return navigationInfoList.size();
    }


    class NavViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        ImageView icon;

        public NavViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.navListText);
            icon = (ImageView) itemView.findViewById(R.id.navListIcon);
        }

        @Override
        public void onClick(View v) {
            if(clickListener!=null){
                clickListener.itemClicked(v,getPosition());
            }
        }
    }

    public interface ClickListener{
         void itemClicked(View view,int position);
    }
}