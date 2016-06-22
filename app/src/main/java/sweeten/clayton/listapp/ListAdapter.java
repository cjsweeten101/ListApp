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

    public String add(String Title, Boolean onCreate, int TeamId , int ParentListId, Context context, ProgressBar progressBar) {

        if(onCreate==false) {

            AsyncCreate asyncCreate = new AsyncCreate(context, progressBar);
            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("title", Title);
                jsonObject.put("teamId", TeamId);
                jsonObject.put("parentListId", ParentListId);
                String results = asyncCreate.execute("http://listaroo.herokuapp.com/api/lists", jsonObject.toString()).get();
                mResults = results;

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

    public String addTeams(String title, int creatorId, Context context, ProgressBar progressBar){

        try {
            AsyncCreate asyncCreate = new AsyncCreate(context, progressBar);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", title);
            jsonObject.put("creatorId", creatorId);
            String result = asyncCreate.execute("http://listaroo.herokuapp.com/api/teams", jsonObject.toString()).get();

            mTitles.add(title);
            notifyItemInserted(mTitles.size());

            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void deleteTeams(int position, int teamId){
        AsyncDelete asyncDelete = new AsyncDelete();
        asyncDelete.execute("http://listaroo.herokuapp.com/api/teams/"+teamId);
        mTitles.remove(position);
        notifyItemRemoved(position);

    }
    public void delete(int position, int id){

            AsyncDelete delete = new AsyncDelete();
            delete.execute("http://listaroo.herokuapp.com/api/lists/" + id);
            mTitles.remove(position);
            notifyItemRemoved(position);

    }

    public String update(String title,int position, int id){
        AsyncUpdate update = new AsyncUpdate();
        JSONObject jsonObject = new JSONObject();
        String results = "";
            try {
                jsonObject.put("title",title);
                results = update.execute("http://listaroo.herokuapp.com/api/lists/"+id,jsonObject.toString()).get();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mTitles.remove(position);
            mTitles.add(position,title);
            notifyItemChanged(position);
        return results;
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
