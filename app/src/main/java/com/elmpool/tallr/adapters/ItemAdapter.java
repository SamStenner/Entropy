package com.elmpool.tallr.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elmpool.tallr.R;
import com.elmpool.tallr.services.Item;
import com.elmpool.tallr.services.Manager;
import com.elmpool.tallr.services.Message;
import com.elmpool.tallr.services.Pin;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> items;
    private FirebaseUser user;
    private LinearLayoutManager llm;

    private int padding;
    private int radius;

    public ItemAdapter(Context context, List<Item> items) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        padding = Manager.convertDimension(context, R.dimen.message_padding);
        radius = Manager.convertDimension(context, R.dimen.message_radius);
        this.items = items;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Item.PIN)
            return new PinViewHolder(createView(parent, R.layout.item_pin));
        if (viewType == Item.MSG)
            return new MessageViewHolder(createView(parent, R.layout.item_msg));
        else if (viewType == Item.MSG_EMOJI)
            return new MessageEmojiViewHolder(createView(parent, R.layout.item_msg_emoji));
        else if (viewType == Item.TYPING)
            return new TypingViewHolder(createView(parent, R.layout.item_typing));
        return null;
    }

    private View createView(@NonNull ViewGroup parent, int layout){
        return LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {
        final Item item = items.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemViewType(int position) {
        return  items.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public abstract class ItemViewHolder extends RecyclerView.ViewHolder {


        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void bind(Item item, int position);

    }

    public class MessageViewHolder extends ItemViewHolder {

        TextView text, name;
        MaterialCardView card;


        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.lbl_message);
            name = itemView.findViewById(R.id.lbl_name);
            card = itemView.findViewById(R.id.card_message);
        }

        @Override
        public void bind(Item item, int position){
            final Message message = (Message) item;
            text.setText(message.getMessage());
            text.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(itemView.getContext(), message.getTime(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            if (message.getUserID().equals(user.getUid()))  decorateSelf(message);
            else decorateOther(message, position);

            boolean prev = checkContinued(message, position, -1);
            boolean after = checkContinued(message, position, 1);

            if(prev) connectPrev();
            else disconnectPrev();

            if (after) connectAfter();
            else disconnectAfter();

        }

        private void changeWidths(Message message, int position, int direction){
            if (position == 0) return;
            boolean sameUser = true;
            int modifier = 1;
            while (sameUser) {
                int next = position - (modifier * direction);
                if (next >= 0 && next < items.size()) {
                    Item itemAdj = items.get(next);
                    if (itemAdj.getType() == Item.MSG) {
                        Message msgAdj = (Message) itemAdj;
                        if (msgAdj.getUserID().equals(message.getUserID())) {
                            modifier ++;
                            continue;
                        }
                    }
                }
                sameUser = false;
            }
            if (modifier == 1) return;
            List<View> views = new ArrayList<>();
            int max = 0;
            for (int i = 0; i <= modifier; i++) {
                View view = llm.findViewByPosition(position - (i * direction));
                if (view != null) {
                    int width = view.findViewById(R.id.card_message).getWidth();
                    views.add(view.findViewById(R.id.card_message));
                    if (width > max) {
                        max = width;
                    }
                }
            }
            for (View view : views) {
                view.getLayoutParams().width = max;
            }
        }

        private boolean checkContinued(Message message, int position, int adj){
            int indexAdj = position + adj;
            if (indexAdj >= 0 && indexAdj < items.size()) {
                Item itemAdj = items.get(indexAdj);
                if (itemAdj.getType() == Item.MSG) {
                    Message msgAdj = (Message) itemAdj;
                    return msgAdj.getUserID().equals(message.getUserID());
                }
            }
            return false;
        }

        private void decorateSelf(Message message){
            Context context = card.getContext();
            card.setCardBackgroundColor(context.getColor(R.color.colorMessageSelf));
            text.setTextColor(context.getColor(R.color.white));
            name.setTextColor(context.getColor(R.color.colorPrimaryText));
            name.setTypeface(null, Typeface.BOLD);
            name.setText(message.getUsername());
        }

        protected void decorateOther(Message message, int position){
            Context context = card.getContext();
            card.setCardBackgroundColor(context.getColor(R.color.colorMessage));
            text.setTextColor(context.getColor(R.color.colorPrimaryText));
            name.setTypeface(null, Typeface.NORMAL);
            name.setVisibility(View.VISIBLE);

            int color = getColors(itemView.getContext(), message.getUserID());
            name.setText(message.getUsername());
            name.setTextColor(color);
        }

        private void connectPrev(){
            itemView.setPadding(
                    itemView.getPaddingLeft(),
                    0,
                    itemView.getPaddingRight(),
                    itemView.getPaddingBottom());
            name.setVisibility(View.GONE);
            card.setShapeAppearanceModel(card.getShapeAppearanceModel()
                    .toBuilder()
                    .setTopLeftCornerSize(0)
                    .setTopRightCornerSize(0)
                    .build());
        }

        private void disconnectPrev(){
            itemView.setPadding(
                    itemView.getPaddingLeft(),
                    padding,
                    itemView.getPaddingRight(),
                    itemView.getPaddingBottom());
            name.setVisibility(View.VISIBLE);
            card.setShapeAppearanceModel(card.getShapeAppearanceModel()
                    .toBuilder()
                    .setTopLeftCornerSize(radius)
                    .setTopRightCornerSize(radius)
                    .build());
        }

        private void connectAfter() {
            itemView.setPadding(
                    itemView.getPaddingLeft(),
                    itemView.getPaddingTop(),
                    itemView.getPaddingRight(),
                    0);
            card.setShapeAppearanceModel(card.getShapeAppearanceModel()
                    .toBuilder()
                    .setBottomLeftCornerSize(0)
                    .setBottomRightCornerSize(0)
                    .build());
        }

        private void disconnectAfter(){
            itemView.setPadding(
                    itemView.getPaddingLeft(),
                    itemView.getPaddingTop(),
                    itemView.getPaddingRight(),
                    padding);
            card.setShapeAppearanceModel(card.getShapeAppearanceModel()
                    .toBuilder()
                    .setBottomLeftCornerSize(radius)
                    .setBottomRightCornerSize(radius)
                    .build());
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
        protected void decorateOther(Message message, int position) {
            super.decorateOther(message, position);
            Context context = card.getContext();
            card.setCardBackgroundColor(context.getColor(android.R.color.transparent));
        }
    }

    public class PinViewHolder extends ItemViewHolder {

        TextView title, text, time;
        MaterialCardView card;

        PinViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.lbl_title);
            text = itemView.findViewById(R.id.lbl_message);
            time = itemView.findViewById(R.id.lbl_time);
            card = itemView.findViewById(R.id.card_message);
        }

        @Override
        public void bind(Item item, int position) {
            Pin pin = (Pin) item;
            title.setText(pin.getTitle(itemView.getContext()));
            text.setText(pin.getMessage());
            time.setText(pin.getTime());
            card.setCardBackgroundColor(pin.getColor(itemView.getContext()));
        }

    }

    public class TypingViewHolder extends ItemViewHolder {

        TypingViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void bind(Item item, int position) {

        }

    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.llm = (LinearLayoutManager) recyclerView.getLayoutManager();
    }
}
