package br.com.bloqueiodechamadas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import br.com.bloqueiodechamadas.R;

import java.util.ArrayList;

public class Adapter extends BaseAdapter {

    private Context context;
    private static ArrayList<String> modelArrayList;

    Adapter(Context context, ArrayList<String> modelArrayList) {
        this.context = context;
        Adapter.modelArrayList = modelArrayList;
    }

    @Override
    public int getCount() {
        return modelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Adapter.ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null && convertView == null) {
            holder = new Adapter.ViewHolder();

            convertView = inflater.inflate(R.layout.item_simple, null, true);

            holder.number = convertView.findViewById(R.id.number);

            convertView.setTag(holder);
        }else {
            holder = (Adapter.ViewHolder) convertView.getTag();
        }

        holder.number.setText(modelArrayList.get(position));

        return convertView;
    }

    public void update(ArrayList<String> array) {
        modelArrayList = array;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        private TextView number;
    }
}
