package me.shouheng.omnilist.adapter;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import me.shouheng.omnilist.PalmApp;
import me.shouheng.omnilist.R;
import me.shouheng.omnilist.model.Alarm;
import me.shouheng.omnilist.model.SubAssignment;
import me.shouheng.omnilist.model.enums.SubAssignmentType;
import me.shouheng.omnilist.utils.ColorUtils;
import me.shouheng.omnilist.utils.ViewUtils;
import me.shouheng.omnilist.widget.tools.IItemTouchHelperAdapter;

/**
 * Created by wangshouheng on 2017/4/4.*/
public class SubAssignmentsAdapter extends BaseMultiItemQuickAdapter<SubAssignmentsAdapter.MultiItem, BaseViewHolder>
        implements IItemTouchHelperAdapter{

    private OnItemRemovedListener onItemRemovedListener;

    private Alarm alarm;
    private String title, comment;

    private boolean isPositionChanged;

    public SubAssignmentsAdapter(List<SubAssignmentsAdapter.MultiItem> data, String title, String comment, Alarm alarm) {
        super(data);
        this.title = title;
        this.alarm = alarm;
        this.comment = comment;
        addItemType(ViewType.HEADER.id, R.layout.item_sub_assignment_header);
        addItemType(ViewType.NORMAL.id, R.layout.item_sub_assignment);
        addItemType(ViewType.FOOTER.id, R.layout.item_sub_assignment_footer);
    }

    public static List<SubAssignmentsAdapter.MultiItem> getMultiItems(@Nullable List<SubAssignment> subAssignments) {
        List<MultiItem> multiItems = new LinkedList<>();
        if (subAssignments == null) return multiItems;

        multiItems.add(new MultiItem(ViewType.HEADER));
        for (SubAssignment subAssignment : subAssignments) {
            multiItems.add(new MultiItem(subAssignment));
        }
        multiItems.add(new MultiItem(ViewType.FOOTER));

        return multiItems;
    }

    public static SubAssignmentsAdapter.MultiItem getMultiItem(SubAssignment subAssignment) {
        return new MultiItem(subAssignment);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItem item) {
        if (ColorUtils.isDarkTheme()) helper.itemView.setBackgroundResource(R.color.dark_theme_background);
        switch (ViewType.getTypeById(helper.getItemViewType())) {
            case HEADER:
                convertHeader(helper);
                break;
            case NORMAL:
                convertBody(helper, item.subAssignment);
                break;
            case FOOTER:
                convertFooter(helper);
                break;
        }
    }

    private void convertHeader(BaseViewHolder helper) {
        helper.addOnClickListener(R.id.tv_title);
        helper.addOnClickListener(R.id.ll_alarm);
        helper.addOnClickListener(R.id.siv_clear);

        helper.setTextColor(R.id.tv_title, ColorUtils.primaryColor());
        helper.setText(R.id.tv_title, TextUtils.isEmpty(title) ? PalmApp.getStringCompact(R.string.click_to_add_title) : title);
        helper.setText(R.id.tv_assignment_alarm, alarm == null ? PalmApp.getStringCompact(R.string.set_date_and_notifications) : alarm.getAlarmInfo(mContext));
        helper.getView(R.id.siv_clear).setVisibility(alarm == null ? View.GONE : View.VISIBLE);
    }

    private void convertBody(BaseViewHolder helper, SubAssignment subAssignment) {
        SubAssignmentType subAssignmentType = subAssignment.getSubAssignmentType();
        boolean showIcon = subAssignmentType == SubAssignmentType.TODO
                || subAssignmentType == SubAssignmentType.NOTE_WITH_PORTRAIT;

        helper.addOnClickListener(R.id.tv_sub_assignment);
        helper.addOnClickListener(R.id.iv_sub_assignment);
        helper.addOnClickListener(R.id.tv_sub_assignment_note);

        helper.getView(R.id.tv_sub_assignment).setVisibility(showIcon ? View.VISIBLE : View.GONE);
        helper.getView(R.id.iv_sub_assignment).setVisibility(showIcon ? View.VISIBLE : View.GONE);
        helper.getView(R.id.tv_sub_assignment_note).setVisibility(showIcon ? View.GONE : View.VISIBLE);
        helper.getView(R.id.divider_note).setVisibility(showIcon ? View.GONE : View.VISIBLE);
        helper.getView(R.id.divider_todo).setVisibility(showIcon ? View.VISIBLE : View.GONE);

        TextView tvSub = helper.getView(R.id.tv_sub_assignment);
        tvSub.setText(subAssignment.getContent());
        tvSub.setAutoLinkMask(Linkify.ALL);
        tvSub.setMovementMethod(LinkMovementMethod.getInstance());
        tvSub.setPaintFlags(subAssignment.isCompleted() ? tvSub.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG : tvSub.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        ViewUtils.setAlpha(tvSub, subAssignment.isCompleted() ? 0.4F : 1F);

        helper.setImageDrawable(R.id.iv_sub_assignment,
                ColorUtils.tintDrawable(PalmApp.getDrawableCompact(
                        subAssignmentType == SubAssignmentType.TODO ?
                                subAssignment.isCompleted() ?
                                        R.drawable.ic_check_box_black_24dp :
                                        R.drawable.ic_check_box_outline_blank_black_24dp :
                                subAssignment.getPortrait().iconRes), ColorUtils.accentColor()));

        helper.setText(R.id.tv_sub_assignment_note, subAssignment.getContent());
    }

    private void convertFooter(BaseViewHolder helper) {
        helper.addOnClickListener(R.id.ll_add_comment);
        helper.addOnClickListener(R.id.ll_add_sub_assignment);
        helper.setText(R.id.tv_write_comments, TextUtils.isEmpty(comment) ? PalmApp.getStringCompact(R.string.click_to_add_comments) : comment);
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        isPositionChanged = true;
        if(fromPosition < toPosition){
            for(int i=fromPosition; i<toPosition; i++){
                Collections.swap(getData(), i, i+1);
            }
        } else{
            for(int i=fromPosition; i > toPosition; i--){
                Collections.swap(getData(), i, i-1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemRemoved(int position, int direction) {
        isPositionChanged = true;
        MultiItem mJustDeletedToDoItem = getData().remove(position);
        notifyItemRemoved(position);
        if (onItemRemovedListener != null){
            onItemRemovedListener.onItemRemoved(mJustDeletedToDoItem, position);
        }
    }

    @Override
    public void afterMoved() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ViewType.HEADER.id;
        }
        if (position == getData().size() + 1){
            return ViewType.FOOTER.id;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void addData(int position, @NonNull MultiItem data) {
        super.addData(position, data);
        isPositionChanged = true;
    }

    @Override
    public void addData(@NonNull MultiItem data) {
        super.addData(data);
        isPositionChanged = true;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyItemChanged(0);
    }

    public String getTitle() {
        return title;
    }

    public void setComment(String comment) {
        this.comment = comment;
        notifyItemChanged(getData().size() - 1);
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
        notifyItemChanged(0);
    }

    public List<SubAssignment> getSubAssignments() {
        List<SubAssignment> list = new LinkedList<>();
        for (MultiItem multiItem : getData()) {
            if (multiItem.viewType == ViewType.NORMAL) {
                list.add(multiItem.subAssignment);
            }
        }
        return list;
    }

    public static class MultiItem implements MultiItemEntity {

        ViewType viewType;

        public SubAssignment subAssignment;

        MultiItem(ViewType viewType) {
            this.viewType = viewType;
        }

        MultiItem(SubAssignment subAssignment) {
            this.subAssignment = subAssignment;
            this.viewType = ViewType.NORMAL;
        }

        @Override
        public int getItemType() {
            return viewType.id;
        }
    }

    public boolean isPositionChanged() {
        return isPositionChanged;
    }

    public void setPositionChanged(boolean positionChanged) {
        isPositionChanged = positionChanged;
    }

    public void recoverItemToPosition(SubAssignment item, int position) {
        getData().add(position, new MultiItem(item));
        notifyItemInserted(position);
    }

    public void setOnItemRemovedListener(OnItemRemovedListener onItemRemovedListener) {
        this.onItemRemovedListener = onItemRemovedListener;
    }

    public interface OnItemRemovedListener {
        void onItemRemoved(SubAssignmentsAdapter.MultiItem item, int position);
    }
}
