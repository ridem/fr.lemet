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

package fr.ybo.transportsbordeaux.modele;

import java.io.Serializable;

import fr.ybo.transportsbordeaux.database.annotation.Colonne;
import fr.ybo.transportsbordeaux.database.annotation.PrimaryKey;
import fr.ybo.transportsbordeaux.database.annotation.Table;

@SuppressWarnings("serial")
@Table
public class ArretFavori implements Serializable {
	@Colonne
	@PrimaryKey
	public String arretId;
	@Colonne
	@PrimaryKey
	public String ligneId;
	@Colonne(type = Colonne.TypeColonne.INTEGER)
	@PrimaryKey
	public Integer macroDirection;
	@Colonne
	public String nomArret;
	@Colonne
	public String direction;
	@Colonne
	public String nomCourt;
	@Colonne
	public String nomLong;
	@Colonne(type = Colonne.TypeColonne.INTEGER)
	public Integer ordre;
}