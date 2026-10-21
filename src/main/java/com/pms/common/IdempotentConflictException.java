package com.pms.common;

/**
 * 幂等冲突：并发下同一 eventId 的事件已被另一请求先落库。
 *
 * <p>该异常用于在事务已因唯一约束冲突被标记 rollback-only 时，仍能把本次请求
 * 识别为「幂等成功」而非 500：由 {@link GlobalExceptionHandler} 捕获并返回 code=0。
 */
public class IdempotentConflictException extends RuntimeException {

    private final Result<?> result;

    public IdempotentConflictException(Result<?> result) {
        this.result = result;
    }

    public Result<?> getResult() {
        return result;
    }
}
