package pe.gob.onpe.consultaopbackend.recaptcha.dto;

import com.fasterxml.jackson.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "success", "challenge_ts", "hostname", "error-codes" })
public class GoogleOutputDto {
	@JsonProperty("success")
	private boolean success;
	@JsonProperty("challenge_ts")
	private String challengeTs;
	@JsonProperty("hostname")
	private String hostname;
	@JsonProperty("score")
	private float score;
	@JsonProperty("action")
	private String action;
	@JsonProperty("error-codes")
	private ErrorCode[] errorCodes;

	private static final Set<ErrorCode> MATCHING_ERRORS = Set.of(
			ErrorCode.INVALIDRESPONSE,
			ErrorCode.MISSINGRESPONSE,
			ErrorCode.BADREQUEST
	);

	enum ErrorCode {
		MISSINGSECRET, INVALIDSECRET, MISSINGRESPONSE, INVALIDRESPONSE, BADREQUEST, TIMEOUTORDUPLICATE;

		private static Map<String, ErrorCode> errorsMap = HashMap.newHashMap(6);

		static {
			errorsMap.put("missing-input-secret", MISSINGSECRET);
			errorsMap.put("invalid-input-secret", INVALIDSECRET);
			errorsMap.put("missing-input-response", MISSINGRESPONSE);
			errorsMap.put("bad-request", INVALIDRESPONSE);
			errorsMap.put("invalid-input-response", BADREQUEST);
			errorsMap.put("timeout-or-duplicate", TIMEOUTORDUPLICATE);
		}

		@JsonCreator
		public static ErrorCode forValue(final String value) {
			return errorsMap.get(value.toLowerCase());
		}
	}

	@JsonProperty("success")
	public boolean isSuccess() {
		return success;
	}

	@JsonProperty("success")
	public void setSuccess(boolean success) {
		this.success = success;
	}

	@JsonProperty("challenge_ts")
	public String getChallengeTs() {
		return challengeTs;
	}

	@JsonProperty("challenge_ts")
	public void setChallengeTs(String challengeTs) {
		this.challengeTs = challengeTs;
	}

	@JsonProperty("hostname")
	public String getHostname() {
		return hostname;
	}

	@JsonProperty("hostname")
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	@JsonProperty("error-codes")
	public void setErrorCodes(ErrorCode[] errorCodes) {
		this.errorCodes = errorCodes;
	}

	@JsonProperty("error-codes")
	public ErrorCode[] getErrorCodes() {
		return errorCodes;
	}

	@JsonProperty("score")
	public float getScore() {
		return score;
	}

	@JsonProperty("score")
	public void setScore(float score) {
		this.score = score;
	}

	@JsonProperty("action")
	public String getAction() {
		return action;
	}

	@JsonProperty("action")
	public void setAction(String action) {
		this.action = action;
	}

	@JsonIgnore
	public boolean hasClientError() {
		final ErrorCode[] errors = getErrorCodes();
		if (errors == null) {
			return false;
		}
		for (final ErrorCode error : errors) {
			if (MATCHING_ERRORS.contains(error)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "GoogleResponse{" + "success=" + success + ", challengeTs='" + challengeTs + '\'' + ", hostname='"
				+ hostname + '\'' + ", score='" + score + '\'' + ", action='" + action + '\'' + ", errorCodes="
				+ Arrays.toString(errorCodes) + '}';
	}
}
