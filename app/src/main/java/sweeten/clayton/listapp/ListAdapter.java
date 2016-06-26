package sweeten.clayton.listapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Clayton on 5/13/2016.
 */
public class ListAdapter extends RecyclerView.Adapter {
    private final ListFragment.onListSelectedInterface mListener;
    Bundle mSavedInstanceState;
    int mPosition;
    List<String> mTitles = new ArrayList<>();
    int mID;
    String mResults = "";

    public ListAdapter(ListFragment.onListSelectedInterface listener, String title, int position) {

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

    public String add(String Title, Boolean onCreate, int TeamId , int ParentListId, Context context, ProgressBar progressBar, int userId, String token) {

        if(onCreate==false) {

            AsyncCreate asyncCreate = new AsyncCreate(context, progressBar);
            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("title", Title);
                jsonObject.put("teamId", TeamId);
                jsonObject.put("parentListId", ParentListId);
                asyncCreate.execute("http://listaroo.herokuapp.com/api/lists", jsonObject.toString(), userId +"", token + "");

                mTitles.add(Title);
                notifyItemInserted(mTitles.size());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            mTitles.add(Title);
            notifyItemInserted(mTitles.size()-1);
        }
        return mResults;
    }

    public void addTeams(String title, int creatorId, Context context, ProgressBar progressBar, int userId, String token){

        try {
            AsyncCreate asyncCreate = new AsyncCreate(context, progressBar);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", title);
            jsonObject.put("creatorId", creatorId);
            asyncCreate.execute("http://listaroo.herokuapp.com/api/teams", jsonObject.toString(), userId +"", token + "");

            mTitles.add(title);
            notifyItemInserted(mTitles.size());



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void deleteTeams(int position, int teamId, int userId, String token){
        AsyncDelete asyncDelete = new AsyncDelete();
        asyncDelete.execute("http://listaroo.herokuapp.com/api/teams/"+teamId, userId +"", token + "");
        mTitles.remove(position);
        notifyItemRemoved(position);


    }
    public void delete(int position, int id, int userId, String token){

            AsyncDelete delete = new AsyncDelete();
            delete.execute("http://listaroo.herokuapp.com/api/lists/" + id, userId+"", token+"");
            mTitles.remove(position);
            notifyItemRemoved(position);

    }

    public void update(String title, int position, int id, ProgressBar progressBar, Context context, int userId, String token){
        AsyncUpdate update = new AsyncUpdate(context, progressBar);
        JSONObject jsonObject = new JSONObject();
        String results = "";
            try {
                jsonObject.put("title",title);
                update.execute("http://listaroo.herokuapp.com/api/lists/"+id,jsonObject.toString(), userId +"", token + "");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            mTitles.remove(position);
            mTitles.add(position,title);
            notifyItemChanged(position);
    }
    public void updateTeam(String title, int position, int id, ProgressBar progressBar, Context context, int userId, String token) {
        try {
            JSONObject jsonObject = new JSONObject();
            AsyncUpdate update = new AsyncUpdate(context, progressBar);
            jsonObject.put("name", title);
            update.execute("http://listaroo.herokuapp.com/api/teams/" + id, jsonObject.toString(), userId+"", token + "");

            mTitles.remove(position);
            mTitles.add(position,title);
            notifyItemChanged(position);

        } catch (JSONException e) {
            e.printStackTrace();
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
