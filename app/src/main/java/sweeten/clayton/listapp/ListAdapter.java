package sweeten.clayton.listapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clayton on 5/13/2016.
 */
public class ListAdapter extends RecyclerView.Adapter {
    private final ListFragment.OnListSelectedInterface mListener;
    Bundle mSavedInstanceState;
    int mPosition;
    List<String> mTitles = new ArrayList<>(50);

    public ListAdapter(ListFragment.OnListSelectedInterface listener, String title, int position) {

        mTitles.add(position,title);
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

    public void add(String Title, int position) {
        mTitles.add(Title);
        notifyItemInserted(position);
    }

    public void delete(int position){

        mTitles.remove(position);
        notifyItemRemoved(position);

    }

    @Override
    public int getItemCount() {
        return mTitles.size();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTextView;
        private int mIndex;
        private ImageButton mImageButton;


        public ListViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.listItems);
            mImageButton = (ImageButton) itemView.findViewById(R.id.deleteButton);
            itemView.setOnClickListener(this);
            mImageButton.setOnClickListener(this);
        }

        public void bindView(int position) {


                if(mTitles.size()>position) {
                mIndex = position;
                mTextView.setText(mTitles.get(position));
            }
        }

        @Override
        public void onClick(View v) {

            if(v.getId()==R.id.deleteButton){

                delete(getAdapterPosition());
            } else {

                mListener.onListSelected(mIndex, mTitles.get(mIndex));


            }
        }
    }
}
