package com.abewy.android.apps.openklyph.messenger.facebook.request;

import java.util.ArrayList;
import org.json.JSONArray;
import com.abewy.android.apps.openklyph.core.graph.GraphObject;
import com.abewy.android.apps.openklyph.core.fql.serializer.FriendDeserializer;
import com.abewy.android.apps.openklyph.messenger.KlyphMessenger;

public class FriendsRequest extends KlyphQuery
{
	@Override
	public boolean isMultiQuery()
	{
		return true;
	}
	
	@Override
	public String getQuery(String id, String offset)
	{
		String query1 = "SELECT uid, name, first_name FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me()) order by name LIMIT 100000";
						//+ getOffset(offset, "0") + ", 50";

		String query2 = "SELECT id, url from square_profile_pic WHERE id IN (SELECT uid FROM #query1) AND size = " + KlyphMessenger.getStandardImageSizeForRequest();
		
		return multiQuery(query1, query2);
	}

	@Override
	public ArrayList<GraphObject> handleResult(JSONArray[] result)
	{
		JSONArray data = result[0];
		JSONArray urls = result[1];
		
		assocData(data, urls, "uid", "id", "pic", "url");
		
		FriendDeserializer deserializer = new FriendDeserializer();
		ArrayList<GraphObject> friends = (ArrayList<GraphObject>) deserializer.deserializeArray(data);
		
		//setHasMoreData(friends.size() >= 50);
		setHasMoreData(false);
		
		return friends;
	}
}
