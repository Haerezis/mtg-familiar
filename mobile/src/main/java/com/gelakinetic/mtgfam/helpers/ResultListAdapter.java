/**
 * Copyright 2011 Adam Feinstein
 * <p/>
 * This file is part of MTG Familiar.
 * <p/>
 * MTG Familiar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * MTG Familiar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with MTG Familiar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gelakinetic.mtgfam.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.text.Html.ImageGetter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.gelakinetic.mtgfam.R;
import com.gelakinetic.mtgfam.helpers.database.CardDbAdapter;

import org.jetbrains.annotations.NotNull;

/**
 * This list adapter is used to display a list of search results. It implements SectionIndexer to enable fast scrolling.
 */
public class ResultListAdapter extends SimpleCursorAdapter {

    private final String[] mFrom;
    private final int[] mTo;
    private final ImageGetter mImgGetter;
    private final Resources.Theme mTheme;
    private final Resources mResources;

    /**
     * Standard Constructor.
     *
     * @param context The context where the ListView associated with this SimpleListItemFactory is running
     * @param cursor  The database cursor. Can be null if the cursor is not available yet.
     * @param from    A list of column names representing the data to bind to the UI. Can be null if the cursor is not
     *                available yet.
     * @param to      The views that should display column in the "from" parameter. These should all be TextViews. The
     *                first N views in this list are given the values of the first N columns in the from parameter.
     *                Can be null if the cursor is not available yet.
     */
    public ResultListAdapter(Context context, Cursor cursor, String[] from, int[] to) {
        super(context, R.layout.result_list_card_row, cursor, from, to, 0);
        this.mFrom = from;
        this.mTo = to;
        this.mResources = context.getResources();
        this.mTheme = context.getTheme();
        this.mImgGetter = ImageGetterHelper.GlyphGetter(context);
    }

    /**
     * Inflates view(s) from the specified XML file.
     *
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the inflated view
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.result_list_card_row, parent, false);
    }

    /**
     * Binds all of the field names passed into the "to" parameter of the constructor with their corresponding cursor
     * columns as specified in the "from" parameter. Binding occurs in two phases. First, if a
     * SimpleCursorAdapter.ViewBinder is available, setViewValue(android.view.View, android.database.Cursor, int) is
     * invoked. If the returned value is true, binding has occurred. If the returned value is false and the view to bind
     * is a TextView, setViewText(TextView, String) is invoked. If the returned value is false and the view to bind is
     * an ImageView, setViewImage(ImageView, String) is invoked. If no appropriate binding can be found, an
     * IllegalStateException is thrown.
     *
     * @param view    Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the correct position.
     */
    @Override
    public void bindView(@NotNull View view, Context context, @NotNull Cursor cursor) {

        boolean hideCost = true;
        boolean hideSet = true;
        boolean hideType = true;
        boolean hideAbility = true;
        boolean hidePT = true;
        boolean hideLoyalty = true;
        boolean hideRarity = true;

        /* make sure these elements are showing (views get recycled) */
        view.findViewById(R.id.cardp).setVisibility(View.VISIBLE);
        view.findViewById(R.id.cardslash).setVisibility(View.VISIBLE);
        view.findViewById(R.id.cardt).setVisibility(View.VISIBLE);

        /* Iterate through the mFrom, find the appropriate view in mTo */
        for (int i = 0; i < mFrom.length; i++) {

            TextView textField = (TextView) view.findViewById(mTo[i]);

            switch (mFrom[i]) {
                case CardDbAdapter.KEY_NAME: {
                    String name = cursor.getString(cursor.getColumnIndex(mFrom[i]));
                    textField.setText(name);
                    break;
                }
                case CardDbAdapter.KEY_MANACOST: {
                    String name = cursor.getString(cursor.getColumnIndex(mFrom[i]));
                    hideCost = false;
                    CharSequence csq = ImageGetterHelper.formatStringWithGlyphs(name, mImgGetter);
                    textField.setText(csq);
                    break;
                }
                case CardDbAdapter.KEY_SET: {
                    char rarity = (char) cursor.getInt(cursor.getColumnIndex(CardDbAdapter.KEY_RARITY));
                    String name = cursor.getString(cursor.getColumnIndex(mFrom[i]));
                    hideSet = false;
                    textField.setText(name);
                    switch (rarity) {
                        case 'c':
                        case 'C':
                            textField.setTextColor(mResources.getColor(getResourceIdFromAttr(R.attr.color_common)));
                            break;
                        case 'u':
                        case 'U':
                            textField.setTextColor(mResources.getColor(getResourceIdFromAttr(R.attr.color_uncommon)));
                            break;
                        case 'r':
                        case 'R':
                            textField.setTextColor(mResources.getColor(getResourceIdFromAttr(R.attr.color_rare)));
                            break;
                        case 'm':
                        case 'M':
                            textField.setTextColor(mResources.getColor(getResourceIdFromAttr(R.attr.color_mythic)));
                            break;
                        case 't':
                        case 'T':
                            textField.setTextColor(mResources.getColor(getResourceIdFromAttr(R.attr.color_timeshifted)));
                            break;
                    }
                    break;
                }
                case CardDbAdapter.KEY_RARITY: {
                    char rarity = (char) cursor.getInt(cursor.getColumnIndex(CardDbAdapter.KEY_RARITY));
                    textField.setText("(" + rarity + ")");
                    hideRarity = false;
                    break;
                }
                case CardDbAdapter.KEY_SUPERTYPE: {
                    String name = CardDbAdapter.getTypeLine(cursor);
                    hideType = false;
                    textField.setText(name);
                    break;
                }
                case CardDbAdapter.KEY_ABILITY: {
                    String name = cursor.getString(cursor.getColumnIndex(mFrom[i]));
                    hideAbility = false;
                    CharSequence csq = ImageGetterHelper.formatStringWithGlyphs(name, mImgGetter);
                    textField.setText(csq);
                    break;
                }
                case CardDbAdapter.KEY_POWER:
                    float p = cursor.getFloat(cursor.getColumnIndex(mFrom[i]));
                    if (p != CardDbAdapter.NO_ONE_CARES) {
                        String pow;
                        hidePT = false;
                        if (p == CardDbAdapter.STAR)
                            pow = "*";
                        else if (p == CardDbAdapter.ONE_PLUS_STAR)
                            pow = "1+*";
                        else if (p == CardDbAdapter.TWO_PLUS_STAR)
                            pow = "2+*";
                        else if (p == CardDbAdapter.SEVEN_MINUS_STAR)
                            pow = "7-*";
                        else if (p == CardDbAdapter.STAR_SQUARED)
                            pow = "*^2";
                        else {
                            if (p == (int) p) {
                                pow = Integer.valueOf((int) p).toString();
                            } else {
                                pow = Float.valueOf(p).toString();
                            }
                        }
                        textField.setText(pow);
                    }
                    break;
                case CardDbAdapter.KEY_TOUGHNESS:
                    float t = cursor.getFloat(cursor.getColumnIndex(mFrom[i]));
                    if (t != CardDbAdapter.NO_ONE_CARES) {
                        hidePT = false;
                        String tou;
                        if (t == CardDbAdapter.STAR)
                            tou = "*";
                        else if (t == CardDbAdapter.ONE_PLUS_STAR)
                            tou = "1+*";
                        else if (t == CardDbAdapter.TWO_PLUS_STAR)
                            tou = "2+*";
                        else if (t == CardDbAdapter.SEVEN_MINUS_STAR)
                            tou = "7-*";
                        else if (t == CardDbAdapter.STAR_SQUARED)
                            tou = "*^2";
                        else {
                            if (t == (int) t) {
                                tou = Integer.valueOf((int) t).toString();
                            } else {
                                tou = Float.valueOf(t).toString();
                            }
                        }
                        textField.setText(tou);
                    }
                    break;
                case CardDbAdapter.KEY_LOYALTY:
                    float l = cursor.getFloat(cursor.getColumnIndex(mFrom[i]));
                    if (l != CardDbAdapter.NO_ONE_CARES) {
                        hideLoyalty = false;
                        if (l == (int) l) {
                            textField.setText(Integer.toString((int) l));
                        } else {
                            textField.setText(Float.toString(l));
                        }
                    }
                    break;
            }
        }

        /* Hide the fields if they should be hidden (didn't exist in mTo)*/
        if (hideCost) {
            view.findViewById(R.id.cardcost).setVisibility(View.GONE);
        }
        if (hideSet) {
            view.findViewById(R.id.cardset).setVisibility(View.GONE);
        }
        if (hideType) {
            view.findViewById(R.id.cardtype).setVisibility(View.GONE);
        }
        if (hideAbility) {
            view.findViewById(R.id.cardability).setVisibility(View.GONE);
        }
        if (!hideLoyalty) {
            view.findViewById(R.id.cardp).setVisibility(View.GONE);
            view.findViewById(R.id.cardslash).setVisibility(View.GONE);
        } else if (hidePT) {
            view.findViewById(R.id.cardp).setVisibility(View.GONE);
            view.findViewById(R.id.cardslash).setVisibility(View.GONE);
            view.findViewById(R.id.cardt).setVisibility(View.GONE);
        }
        if (hideRarity) {
            view.findViewById(R.id.rarity).setVisibility(View.GONE);
        }
    }

    /**
     * This helper function translates an attribute into a resource ID
     *
     * @param attr The attribute ID
     * @return the resource ID
     */
    private int getResourceIdFromAttr(int attr) {
        TypedArray ta = mTheme.obtainStyledAttributes(new int[]{attr});
        assert ta != null;
        int resId = ta.getResourceId(0, 0);
        ta.recycle();
        return resId;
    }
}