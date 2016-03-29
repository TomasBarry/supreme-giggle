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
    private ArrayList<UserPermission> usersAndPerms;

    private LayoutInflater mInflater;

    private String fileName;

    public MyCustomBaseAdapter(Context context, ArrayList<UserPermission> results, String fileName) {
        this.usersAndPerms = results;
        this.mInflater = LayoutInflater.from(context);
        this.fileName = fileName;
    }

    public int getCount() {
        return usersAndPerms.size();
    }

    public Object getItem(int position) {
        return usersAndPerms.get(position);
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
                    usersAndPerms.get(holder.id).flipPermission();
                    Log.i("switch clicked", "The clicked switch for user "
                            + usersAndPerms.get(holder.id).getUserName()
                            + " was at " + !usersAndPerms.get(holder.id).getPermission()
                            + " is at " + usersAndPerms.get(holder.id).getPermission());
                    if (usersAndPerms.get(holder.id).getPermission()) {
                        Log.i("switch clicked", "Adding permission on file " + fileName);
                        MainActivity.databaseController.printTable("FileKeys");
                        MainActivity.databaseController.addPermissionFor(usersAndPerms.get(holder.id).getUserName(), fileName);
                        MainActivity.databaseController.printTable("FileKeys");
                        Log.i("switch clicked", "Added permission on file " + fileName);
                    } else {
                        Log.i("switch clicked", "Revoking permission on file " + fileName);
                        MainActivity.databaseController.printTable("FileKeys");
                        MainActivity.databaseController.revokePermissionsFor(usersAndPerms.get(holder.id).getUserName(), fileName);
                        MainActivity.databaseController.printTable("FileKeys");
                        Log.i("switch clicked", "Revoked permission on file " + fileName);
                    }
                }
            });

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.userName.setText(usersAndPerms.get(position).getUserName());
        holder.permission.setChecked(usersAndPerms.get(position).getPermission());
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
