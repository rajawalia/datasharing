package com.digicert.tlm.plugin.ca;

/**
 * Thrown by a CA plugin. {@code retryable=true} tells TLM the failure is transient
 * (timeouts, 429/5xx) so the order may be retried; false marks it permanent.
 * Never put secrets in the message: it is shown in the TLM UI and logs.
 */
public class CaPluginException extends RuntimeException {
    private final boolean retryable;

    public CaPluginException(String message, boolean retryable) { super(message); this.retryable = retryable; }
    public CaPluginException(String message, boolean retryable, Throwable cause) { super(message, cause); this.retryable = retryable; }

    public boolean isRetryable() { return retryable; }
}
