from typing import Iterator, Tuple, Optional

from hbutils.binary import c_uint, c_int, c_long, c_buffer
from hbutils.file import is_eof

from .model import EventType, ThreadState

_MAGIC_NUMBER = 0xdeadbeef


def load_from_file(filename: str) \
        -> Iterator[Tuple[EventType, int, float, Optional[str], Optional[ThreadState]]]:
    _first_time = None
    with open(filename, 'rb') as file_:
        assert c_uint.read(file_) == _MAGIC_NUMBER

        while not is_eof(file_):
            type_: EventType = EventType(c_int.read(file_))
            thread_id: int = c_int.read(file_)

            secs = c_long.read(file_)
            nanos = c_long.read(file_)
            time_ = secs + nanos / 1e9
            if _first_time is None:
                _first_time = time_
            rel_time = time_ - _first_time

            name, state = None, None
            if type_ == 0:
                name = c_buffer(51).read(file_)
                name = name.rstrip(b'\x00')
            elif type_ == 2:
                state = ThreadState(c_int.read(file_))

            yield type_, thread_id, rel_time, repr(name), state
