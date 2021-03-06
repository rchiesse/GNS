package edu.umass.cs.gnsserver.gnsapp.packet;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import edu.umass.cs.gnscommon.GNSProtocol;
import edu.umass.cs.gnscommon.exceptions.server.InternalRequestException;
import edu.umass.cs.gnscommon.packets.CommandPacket;
import edu.umass.cs.gnscommon.packets.PacketUtils;
import edu.umass.cs.gnsserver.gnsapp.GNSApp;
import edu.umass.cs.gnsserver.gnsapp.GNSCommandInternal;
import edu.umass.cs.gnsserver.interfaces.InternalRequestHeader;
import edu.umass.cs.utils.DefaultTest;
import edu.umass.cs.utils.Util;

/**
 * @author arun
 * 
 *         A {@link CommandPacket} used to send internal as well as active GNS
 *         requests. These requests should only be accepted on the MUTUAL_AUTH
 *         port, so they can be initiated only by trusted servers or admin
 *         clients. If not for this rectriction,
 *
 */
public class InternalCommandPacket extends CommandPacket implements
		InternalRequestHeader {

	/**
	 * Decremented at each hop when an active request issues a remote query. The
	 * decrement to this final field is done by the sender automatically in the
	 * toString() or toBytes() method that serializes this object.
	 */
	private final int ttl;

	/**
	 * The GUID that originated this active request. This is the GUID that gets
	 * "charged" for all of the resources (transitively) consumed by the
	 * originating request. This GUID is set when a {@link CommandPacket} is
	 * first received by any GNS server via
	 * {@link GNSApp#execute(edu.umass.cs.gigapaxos.interfaces.Request)} and
	 * remains unchanged until the corresponding response goes back to the
	 * end-client that originated the {@link CommandPacket}.
	 * 
	 */
	private final String originatingGUID;

	/**
	 * The request ID that originated this active request chain. This ID is set
	 * when a {@link CommandPacket} is first received by any GNS server via
	 * {@link GNSApp#execute(edu.umass.cs.gigapaxos.interfaces.Request)} and
	 * remains unchanged until the corresponding response goes back to the
	 * end-client that originated the {@link CommandPacket}.
	 * 
	 */
	private final long originatingRequestID;

	/**
	 * The only place where this flag is set to false. After this point, it can
	 * only ever be changed to true.
	 */
	private boolean hasBeenCoordinatedOnce = false;

	// not sure whether to store inside or outside, maybe doesn't matter
	private static final boolean STORE_INSIDE = true;

	/**
	 * Whether {@link InternalCommandPacket} has a separate packet type. True
	 * means the type has to be registered as a MUTUAL_AUTH type. False is only
	 * for temporary backwards compatibility and will be changed to true.
	 */
	public static final boolean SEPARATE_INTERNAL_TYPE = false;

	// for testing
	private static boolean decrementTTL = true;

	/**
	 * This constructor is invoked exactly once in a request chain, namely at
	 * the beginning of the chain, which is why the constructor does not take an
	 * originatingRequestID argument but instead inherits it from the parent
	 * CommandPacket. At subsequent hops, the originatingRequestID will be read
	 * directly from the serialized form of this request and preserved
	 * throughout the chain.
	 * 
	 * @param ttl
	 * @param oGUID
	 * @param command
	 * @throws JSONException
	 */
	InternalCommandPacket(Integer ttl, String oGUID, long oqid,
			JSONObject command) throws JSONException {
		super(
		// this' requestID, different from originatingRequestID
				(long) (Math.random() * Long.MAX_VALUE),

				STORE_INSIDE ? command
						.put(GNSProtocol.ORIGINATING_GUID.toString(), oGUID)
						/**
						 * We put ttl as String because
						 * {@link CommandPacket#toBytes} expects all keys and
						 * values except commandType to be strings.
						 */
						.put(GNSProtocol.REQUEST_TTL.toString(), ttl)

						.put(GNSProtocol.ORIGINATING_QID.toString(), oqid)

				: command);
		this.setType(SEPARATE_INTERNAL_TYPE ? Packet.PacketType.INTERNAL_COMMAND
				: Packet.PacketType.COMMAND);
		this.ttl = ttl;
		this.originatingGUID = oGUID;
		this.originatingRequestID = oqid;
		// hasBeenCoordinatedOnce already initialized
	}

	protected InternalCommandPacket(InternalRequestHeader header,
			JSONObject command) throws JSONException {
		this(header.getTTL(), header.getOriginatingGUID(), header
				.getOriginatingRequestID(), command);
	}

	public JSONObject toJSONObject() throws JSONException {
		JSONObject json = super.toJSONObject();
		return STORE_INSIDE && decrementTTL
				&&
				// decrement TTL, rest is all set
				json.getJSONObject(GNSProtocol.QUERY.toString())
						.put(GNSProtocol.REQUEST_TTL.toString(),
								json.getJSONObject(GNSProtocol.QUERY.toString())
										.getInt(GNSProtocol.REQUEST_TTL
												.toString()) - 1) != null ? json

				: // !STORE_INSIDE
					// decrementing TTL at sender
				json.put(GNSProtocol.REQUEST_TTL.toString(),
						this.ttl - (decrementTTL ? 1 : 0))
						// put the other header fields as well
						.put(GNSProtocol.ORIGINATING_GUID.toString(),
								this.originatingGUID)
						.put(GNSProtocol.ORIGINATING_QID.toString(),
								this.originatingRequestID)
						.put(GNSProtocol.COORD1.toString(),
								this.hasBeenCoordinatedOnce);
	}

	/**
	 * @param json
	 * @throws JSONException
	 */
	public InternalCommandPacket(JSONObject json) throws JSONException {
		super(json);
		this.setType(SEPARATE_INTERNAL_TYPE ? Packet.PacketType.INTERNAL_COMMAND
				: Packet.PacketType.COMMAND);

		this.ttl = (Integer) getInOrOutside(GNSProtocol.REQUEST_TTL.toString(),
				json);
		this.originatingGUID = (String) getInOrOutside(
				GNSProtocol.ORIGINATING_GUID.toString(), json);
		Object id = getInOrOutside(GNSProtocol.ORIGINATING_QID.toString(), json);
		this.originatingRequestID = id instanceof Integer ? ((Integer) id)
				.longValue() : ((Long) id).longValue();
		this.hasBeenCoordinatedOnce = (Boolean) getInOrOutside(
				GNSProtocol.COORD1.toString(), json);
	}

	private static final Object getInOrOutside(String key, JSONObject outerJSON)
			throws JSONException {
		return STORE_INSIDE ? outerJSON.getJSONObject(
				GNSProtocol.QUERY.toString()).get(key) : outerJSON.get(key);
	}

	@Override
	public long getOriginatingRequestID() {
		return this.originatingRequestID;
	}

	@Override
	public String getOriginatingGUID() {
		return this.originatingGUID;
	}

	@Override
	public int getTTL() {
		return this.ttl;
	}

	@Override
	public boolean hasBeenCoordinatedOnce() {
		return false;
	}

	private static InternalRequestHeader getTestHeader(int ttl, String GUID,
			long qid) {
		return new InternalRequestHeader() {

			@Override
			public long getOriginatingRequestID() {
				return qid;
			}

			@Override
			public String getOriginatingGUID() {
				return GUID;
			}

			@Override
			public int getTTL() {
				return ttl;
			}

			@Override
			public boolean hasBeenCoordinatedOnce() {
				return false;
			}

		};
	}

	/**
	 * For testing.
	 */
	public static class InternalCommandPacketTest extends DefaultTest {

		/**
		 * @throws JSONException
		 * @throws InternalRequestException 
		 */
		@Test
		public void test_01_serialization() throws JSONException, InternalRequestException {
			Util.assertAssertionsEnabled();
			decrementTTL = false;
			InternalCommandPacket icmd = GNSCommandInternal.fieldRead("hello",
					"world", getTestHeader(4, "randomGUID", 312312312));
			String s1 = (icmd.toString());
			String s2 = new InternalCommandPacket(new JSONObject(s1))
					.toString();
			// assumes canonical order
			org.junit.Assert.assertEquals(s1, s2);
		}
	}
}
