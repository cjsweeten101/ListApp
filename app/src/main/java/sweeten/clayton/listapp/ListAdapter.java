package sweeten.clayton.listapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Clayton on 5/13/2016.
 */
public class ListAdapter extends RecyclerView.Adapter {
    private final ListFragment.OnListSelectedInterface mListener;
    Bundle mSavedInstanceState;
    int mPosition;
    List<String> mTitles = new ArrayList<>();
    int mID;
    String mResults = "";

    public ListAdapter(ListFragment.OnListSelectedInterface listener,String title, int position) {

       // mTitles.add(title);
        mPosition = position;
        mListener = listener;


    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem, parent, false);
        return new ListViewHolder(view);
    }

    @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(mTitles.size()>position) {

            ((ListViewHolder) holder).bindView(position);

        }
    }

    public String add(String Title,Boolean onCreate, int position) {

        if(onCreate==false) {

            AsyncCreate asyncCreate = new AsyncCreate();
            JSONObject jsonObject = new JSONObject();
            try {
                JSONObject listItem = new JSONObject();
                if(position==0){
                    jsonObject.put("title", Title);
                    listItem = jsonObject;
                    String results = asyncCreate.execute("http://listaroo.herokuapp.com/api/lists", listItem.toString()).get();
                    mResults = results;
                } else {
                    jsonObject.put("content", Title);
                    jsonObject.put("list_id", position);
                    listItem = new JSONObject();
                    listItem.put("list_item", jsonObject);
                    String results = asyncCreate.execute("http://listaroo.herokuapp.com/api/list_items", listItem.toString()).get();
                    mResults = results;
                }

                mTitles.add(Title);
                notifyItemInserted(mTitles.size());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            mTitles.add(Title);
            notifyItemInserted(mTitles.size()-1);
        }
        return mResults;
    }

    public void delete(int position, Boolean List, int id){
        if(List==false) {
            AsyncDelete delete = new AsyncDelete();
            delete.execute("http://listaroo.herokuapp.com/api/list_items/" + id);
            mTitles.remove(position);
            notifyItemRemoved(position);
        } else {
            AsyncDelete delete = new AsyncDelete();
            delete.execute("http://listaroo.herokuapp.com/api/lists/" + id);
            mTitles.remove(position);
            notifyItemRemoved(position);
        }

    }

    public void update(String title,int position,  Boolean List, int id){
        AsyncUpdate update = new AsyncUpdate();
        JSONObject jsonObject = new JSONObject();
        if(List==false) {
            try {
                jsonObject.put("content",title);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                update.execute("http://listaroo.herokuapp.com/api/list_items/"+id,jsonObject.toString()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            mTitles.remove(position);
            mTitles.add(position,title);
            notifyItemChanged(position);
        } else {
            try {
                jsonObject.put("title",title);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                update.execute("http://listaroo.herokuapp.com/api/lists/"+id,jsonObject.toString()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            mTitles.remove(position);
            mTitles.add(position,title);
            notifyItemChanged(position);
        }
    }

    @Override
    public int getItemCount() {
        return mTitles.size();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageButton mEditButton;
        private TextView mTextView;
        private int mIndex;
        private ImageButton mImageButton;


        public ListViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.listItems);
            mImageButton = (ImageButton) itemView.findViewById(R.id.deleteButton);
            mEditButton = (ImageButton) itemView.findViewById(R.id.editButton);
            itemView.setOnClickListener(this);
            mImageButton.setOnClickListener(this);
            mEditButton.setOnClickListener(this);
        }

        public void bindView(int position) {


                if(mTitles.size()>position) {
                mIndex = position;
                mTextView.setText(mTitles.get(position));
            }
        }

        @Override
        public void onClick(View v) {

          //  if(v.getId()==R.id.deleteButton){

             //   mDeleteInterface.onListDelete(getAdapterPosition());

           // } else {

               mListener.onListSelected(getAdapterPosition(), mTitles.get(getAdapterPosition()), v);

            //}
        }
    }
}
