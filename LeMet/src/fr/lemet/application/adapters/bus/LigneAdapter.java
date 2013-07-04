/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.lemet.application.adapters.bus;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.lemet.application.R;
import fr.lemet.application.keolis.KeolisException;
import fr.lemet.transportscommun.donnees.modele.Ligne;
import fr.lemet.transportscommun.util.IconeLigne;

public class LigneAdapter extends BaseAdapter {
     public Typeface font;

    static class ViewHolder {
        TextView nomLong;
        ImageView iconeLigne;
    }

    private final LayoutInflater mInflater;

    private final List<Ligne> lignes;

    public LigneAdapter(Context context, List<Ligne> lignes) throws KeolisException {
        mInflater = LayoutInflater.from(context);
        this.lignes = lignes;
        font = Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Reg.ttf");
    }

    public int getCount() {
        return lignes.size();
    }

    public Ligne getItem(int position) {
        return lignes.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View convertView1 = convertView;
        LigneAdapter.ViewHolder holder;
        if (convertView1 == null) {
            convertView1 = mInflater.inflate(R.layout.ligne, null);
            holder = new LigneAdapter.ViewHolder();
            holder.iconeLigne = (ImageView) convertView1.findViewById(R.id.iconeLigne);
            holder.nomLong = (TextView) convertView1.findViewById(R.id.nomLong);
            holder.nomLong.setTypeface(font);
            convertView1.setTag(holder);
        } else {
            holder = (LigneAdapter.ViewHolder) convertView1.getTag();
        }
        Ligne ligne = lignes.get(position);
        holder.nomLong.setText(ligne.nomLong);
        holder.iconeLigne.setImageResource(IconeLigne.getIconeResource(ligne.nomCourt));
        return convertView1;
    }
}