package tomas.giggle;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class MyCustomBaseAdapter extends BaseAdapter {
    private static ArrayList<UserPermission> searchArrayList;

    private LayoutInflater mInflater;

    public MyCustomBaseAdapter(Context context, ArrayList<UserPermission> results) {
        this.searchArrayList = results;
        this.mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return searchArrayList.size();
    }

    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_view_row_text_and_radio, parent, false);
            holder =
                    new ViewHolder((TextView) convertView.findViewById(R.id.user_name),
                            (Switch) convertView.findViewById(R.id.permission_switch),
                            position);
            holder.permission.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.i("switch clicked", "The clicked switch: " + holder.id);
                }
            });

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.userName.setText(searchArrayList.get(position).getUserName());
        holder.permission.setChecked(searchArrayList.get(position).getPermission());
        return convertView;
    }

    public class ViewHolder {
        public TextView userName;
        public Switch permission;
        public int id;

        ViewHolder(TextView userName, Switch permission, int id) {
            this.userName = userName;
            this.permission = permission;
            this.id = id;
        }
    }
}
