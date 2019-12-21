package com.elmpool.tallr.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elmpool.tallr.R;
import com.elmpool.tallr.models.Item;
import com.elmpool.tallr.utils.Manager;
import com.elmpool.tallr.models.Message;
import com.elmpool.tallr.models.Pin;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;

public class ItemAdapter extends FirebaseRecyclerAdapter<Item, ItemAdapter.ItemViewHolder> {

    private FirebaseUser user;
    private RecyclerView recycler;
    private Context context;
    private boolean animate;

    public ItemAdapter(@NonNull FirebaseRecyclerOptions options, @NotNull FirebaseUser user, Context context) {
        super(options);
        this.user = user;
        this.context = context;
        observe();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case Item.PIN: return new PinViewHolder(createView(parent, R.layout.item_pin));
            case Item.MSG: return new MessageViewHolder(createView(parent, R.layout.item_msg));
            case Item.MSG_EMOJI: return new MessageEmojiViewHolder(createView(parent, R.layout.item_msg_emoji));
            default: throw new IllegalArgumentException();
        }
    }

    private View createView(@NonNull ViewGroup parent, int layout){
        return LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ItemViewHolder holder, int position, @NonNull Item model) {
        holder.bind(model, position);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        for (int i=0; i < getItemCount(); i++){
            Item item = getItem(i);
            if (item.getType() == Item.MSG) {
                Message message = (Message) item;
                int max = scan(message, i, 1);
                int min = scan(message, i, -1);
                setWidths(message, min, max);
                if (min==max) message.setFlags(Message.TYPE_SINGLE);
                else if (i==max) message.setFlags(Message.TYPE_BOTTOM);
                else if (i==min) message.setFlags(Message.TYPE_TOP);
                else message.setFlags(Message.TYPE_CENTER);
            }
        }
    }

    private int scan(Message message, int position, int direction){
        if (position >= 0 && position < getItemCount()) {
            Item item = getItem(position);
            if (item.getType() == Item.MSG) {
                Message neighbour = (Message) item;
                if (message.sameUser(neighbour) && message.syncTime(neighbour)) {
                    return scan(neighbour,position + direction, direction);
                }
            }
        }
        return position - direction;
    }

    private void setWidths(Message message, int min, int max){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screen = (int)(displayMetrics.widthPixels*0.875);
        int maxWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        for (int i=min; i<=max; i++) {
            Message other = (Message) getItem(i);
            TextPaint paint = new TextPaint();
            paint.setTextSize(Manager.convertDimension(context, R.dimen.message_text_size));
            String text = other.getText();

            StaticLayout layout = new StaticLayout(text, paint, screen,
                    Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            int width = (int) layout.getLineMax(0);
            if (width > maxWidth) maxWidth = width;
        }

        message.setViewWidth(maxWidth);

        for (int i=min; i<=max; i++) {
            Message other = (Message) getItem(i);
            if (other.getViewWidth() < maxWidth) {
                message.setViewWidth(maxWidth);
            }
        }
    }

    private void observe(){
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (recycler != null) {
                    int count = getItemCount();
                    int last = ((LinearLayoutManager) recycler.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                    if (last == -1 || positionStart >= (count - 1) && last == (positionStart - 1)) {
                        recycler.scrollToPosition(positionStart);

                    }
                }
                if (positionStart == getItemCount()-1) {
                    Item item = getItem(positionStart);
                    if (item.getType() == Item.MSG) {
                        Message message = (Message) getItem(positionStart);
                        int min = scan(message, positionStart, -1);
                        for (int i=min; i<positionStart; i++) {
                            notifyItemChanged(i);
                        }
                    }
                }
            }
        });
    }

    public abstract class ItemViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{


        TextView time;
        MaterialCardView card;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.lbl_time);
            card = itemView.findViewById(R.id.card_message);
            itemView.setOnClickListener(this);
        }

        public abstract void bind(Item item, int position);

        @Override
        public void onClick(View view) {
            showTime();
        }

        private void showTime(){
            int position = getAdapterPosition();
            if (animate && position != -1) {
                Item item = getItem(position);
                TextView time = this.time;
                if (item.getType() == Item.MSG) {
                    Message message = (Message) item;
                    int min = scan(message, position, -1);
                    View other = recycler.getLayoutManager().findViewByPosition(min);
                    if (other == null) return;
                    time = other.findViewById(R.id.lbl_time);
                }
                if ((boolean) time.getTag()) return;
                TransitionManager.beginDelayedTransition(recycler, new AutoTransition());
                int visible = time.getVisibility();
                Handler handler = new Handler();
                if (visible == View.GONE) {
                    int delay = 3500;
                    final TextView finalTime = time;
                    finalTime.setVisibility(View.VISIBLE);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (finalTime.getVisibility() == View.VISIBLE) {
                                TransitionManager.beginDelayedTransition(recycler, new AutoTransition());
                                finalTime.setVisibility(View.GONE);
                            }
                        }
                    }, delay);
                } else {
                    time.setVisibility(View.GONE);
                }
            }
        }


    }

    public class MessageViewHolder extends ItemViewHolder {

        TextView text, name;
        Message message;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.lbl_message);
            name = itemView.findViewById(R.id.lbl_name);
        }

        @Override
        public void bind(Item item, int position){
            this.message = (Message) item;
            text.setText(message.getText());

            if (message.getUserID().equals(user.getUid()))  decorateSelf(message);
            else decorateOther(message);
            showTime(position);
            format();

        }

        private void showTime(int position){
            boolean sync = position != 0;
            if (sync && message.getFlags() == Message.TYPE_TOP
                    || message.getFlags() == Message.TYPE_SINGLE) {
                if (position-1 != -1) {
                    Item other = getItem(position - 1);
                    sync = message.syncTime(other, 30 * 60);
                }
            }
            time.setVisibility(sync ? View.GONE : View.VISIBLE);
            time.setTag(!sync);
            time.setText(message.getTime());
            int padding = sync ?
                    Manager.convertDimension(context, R.dimen.time_padding_collapsed) :
                    Manager.convertDimension(context, R.dimen.time_padding);
            time.setPadding(
                    time.getPaddingLeft(),
                    padding,
                    time.getPaddingRight(),
                    time.getPaddingBottom());
        }

        private void format() {
            int padding = Manager.convertDimension(context, R.dimen.message_padding);
            int radius = Manager.convertDimension(context, R.dimen.message_corner_radius);
            switch (message.getFlags()) {
                case Message.TYPE_TOP: shape(radius, 0, padding, 0, View.VISIBLE); break;
                case Message.TYPE_CENTER: shape(0, 0, 0, 0, View.GONE); break;
                case Message.TYPE_BOTTOM: shape(0, radius, 0, padding, View.GONE); break;
                default: shape(radius, radius, padding, padding, View.VISIBLE); break;
            }
        }

        private void shape(int radiusTop, int radiusBottom, int paddingTop, int paddingBottom, int visibility) {
            name.setVisibility(visibility);
            card.setShapeAppearanceModel(card.getShapeAppearanceModel()
                    .toBuilder()
                    .setTopLeftCornerSize(radiusTop)
                    .setTopRightCornerSize(radiusTop)
                    .setBottomLeftCornerSize(radiusBottom)
                    .setBottomRightCornerSize(radiusBottom)
                    .build());
            itemView.setPadding(
                    itemView.getPaddingLeft(),
                    paddingTop,
                    itemView.getPaddingRight(),
                    paddingBottom);
            int width = message.getViewWidth();
            ViewGroup.LayoutParams params = text.getLayoutParams();
            params.width = width;
            text.setLayoutParams(params);
        }

        protected void decorateSelf(Message message){
            Context context = card.getContext();
            card.setCardBackgroundColor(Manager.getColor(context, R.color.colorMessageSelf));
            text.setTextColor(Manager.getColor(context, R.color.white));
            name.setTextColor(Manager.getColor(context, R.color.colorPrimaryText));
            name.setTypeface(ResourcesCompat.getFont(context, R.font.google_sans_bold));
            name.setText(message.getUsername());
        }

        protected void decorateOther(Message message){
            Context context = card.getContext();
            card.setCardBackgroundColor(Manager.getColor(context, R.color.colorMessage));
            text.setTextColor(Manager.getColor(context, R.color.colorPrimaryText));
            name.setTypeface(ResourcesCompat.getFont(context, R.font.google_sans_regular));
            name.setVisibility(View.VISIBLE);

            int color = getColors(itemView.getContext(), message.getUserID());
            name.setText(message.getUsername());
            name.setTextColor(color);
        }

        private int getColors(Context context, String userID){
            String[] colors = context.getResources().getStringArray(R.array.username_colors);
            int total = 0;
            for (char c : userID.toCharArray()) total += (int) c;
            int index = total % (colors.length-1);
            return Color.parseColor(colors[index]);
        }

    }

    public class MessageEmojiViewHolder extends MessageViewHolder {

        MessageEmojiViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void bind(Item item, int position) {
            super.bind(item, position);
            card.setCardBackgroundColor(Manager.getColor(context, android.R.color.transparent));
        }

    }

    public class PinViewHolder extends ItemViewHolder {

        TextView title, text;

        PinViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.lbl_title);
            text = itemView.findViewById(R.id.lbl_message);
        }

        @Override
        public void bind(Item item, int position) {
            Pin pin = (Pin) item;
            title.setText(pin.getTitle(itemView.getContext()));
            text.setText(pin.getMessage());
            time.setText(pin.getTime());
            card.setCardBackgroundColor(pin.getColor(itemView.getContext()));
            time.setTag(true);
        }

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recycler = recyclerView;
    }

    @Override
    public void stopListening() {
        super.stopListening();
        this.animate = false;
    }

    @Override
    public void startListening() {
        super.startListening();
        this.animate = true;
    }
}
