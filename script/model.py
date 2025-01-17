from enum import IntEnum, unique, IntFlag


@unique
class EventType(IntEnum):
    START = 0
    END = 1
    STATE_CHANGE = 2


@unique
class ThreadState(IntFlag):
    ALIVE = 0x0001
    TERMINATED = 0x0002
    RUNNABLE = 0x0004

    WAITING_INDEFINITELY = 0x0010
    WAITING_WITH_TIMEOUT = 0x0020
    SLEEPING = 0x0040
    WAITING = 0x0080

    IN_OBJECT_WAIT = 0x0100
    PARKED = 0x0200
    BLOCKED_ON_MONITOR_ENTER = 0x0400

    SUSPENDED = 0x100000
    INTERRUPTED = 0x200000
    IN_NATIVE = 0x400000

    VENDOR_1 = 0x10000000
    VENDOR_2 = 0x20000000
    VENDOR_3 = 0x40000000
