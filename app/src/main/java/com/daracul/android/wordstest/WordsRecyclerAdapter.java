package com.daracul.android.wordstest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import static com.daracul.android.wordstest.MainActivity.CONTEXT_MENU_DELETE;
import static com.daracul.android.wordstest.MainActivity.CONTEXT_MENU_RENAME;

public class WordsRecyclerAdapter extends RecyclerView.Adapter<WordsRecyclerAdapter.WordsViewHolder> {
    private Context mContext;
    private List<Word> wordsList;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public WordsRecyclerAdapter(Context mContext, List<Word> wordsList) {
        this.mContext = mContext;
        this.wordsList = wordsList;
    }

    @Override
    public WordsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.word_item_container, parent, false);
        return new WordsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WordsViewHolder holder, int position) {
        Word currentWord = wordsList.get(position);
        holder.wordTextView.setText(currentWord.getName());
        holder.translationTextView.setText(currentWord.getTranslation());

        // we add this listener to get position of holder for context menu uses
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getPosition());
                return false;
            }
        });
    }

    // removing listener that there are no reference issues, may not be required
    @Override
    public void onViewRecycled(WordsViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        if (wordsList==null) return 0;
        return wordsList.size();
    }

    public void swapCursor(List<Word> wordsList) {
        this.wordsList = wordsList;
        notifyDataSetChanged();
    }


    public class WordsViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView wordTextView;
        TextView translationTextView;

        public WordsViewHolder(View itemView) {
            super(itemView);
            wordTextView = (TextView) itemView.findViewById(R.id.word);
            translationTextView = (TextView) itemView.findViewById(R.id.translation);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, CONTEXT_MENU_RENAME, 0, R.string.context_rename);
            menu.add(0, CONTEXT_MENU_DELETE, 0, R.string.context_delete);
        }

    }
}
