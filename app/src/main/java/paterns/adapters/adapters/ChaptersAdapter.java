package paterns.adapters.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import Class.Chapter;
import com.example.bookworm.R;

public class ChaptersAdapter extends RecyclerView.Adapter<ChaptersAdapter.ViewHolder> {
    private List<Chapter> chapters;
    private OnItemClickListener listener;
    private int currentPlayingPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(Chapter chapter);
    }

    public ChaptersAdapter(List<Chapter> chapters, OnItemClickListener listener) {
        this.chapters = chapters;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chapter chapter = chapters.get(position);
        boolean isPlaying = position == currentPlayingPosition;
        holder.bind(chapter, listener, isPlaying);
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public void setCurrentPlayingPosition(int position) {
        int previousPosition = currentPlayingPosition;
        currentPlayingPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(currentPlayingPosition);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView chapterTitle;

        ViewHolder(View itemView) {
            super(itemView);
            chapterTitle = itemView.findViewById(R.id.chapterTitle);
        }

        void bind(Chapter chapter, OnItemClickListener listener, boolean isPlaying) {
            chapterTitle.setText(chapter.getTitle());

            // Зміна фону для вибраного елемента на світло-сірий
            if (isPlaying) {
                itemView.setBackgroundColor(0xFFD3D3D3);  // Світло-сірий фон для вибраного елемента
                chapterTitle.setTextColor(0xFF000000);    // Чорний колір тексту для вибраного елемента
            } else {
                itemView.setBackgroundColor(0xFFFFFFFF);  // Білий фон для невибраного елемента
                chapterTitle.setTextColor(0xFF000000);    // Чорний колір тексту для невибраного елемента
            }

            itemView.setOnClickListener(v -> listener.onItemClick(chapter));
        }

    }
}
