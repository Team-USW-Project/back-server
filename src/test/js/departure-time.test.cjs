const {test} = require('node:test');
const assert = require('node:assert/strict');
const schedule = require('../../main/resources/static/departure-time.js');
const now = Date.parse('2026-10-14T09:00:00+09:00');
test('19:00 appointment, 50 minute travel, 10 minute buffer => 18:00 departure', () => {
    const r = schedule.calculate('2026-10-14T19:00', 3000, 10, now);
    assert.equal(r.departure, Date.parse('2026-10-14T18:00:00+09:00'));
    assert.equal(r.arrival, Date.parse('2026-10-14T18:50:00+09:00'));
    assert.equal(r.status, 'OK');
});
test('midnight appointment carries departure into previous date', () => {
    const r = schedule.calculate('2026-10-15T00:05', 3600, 10, now);
    assert.equal(r.departure, Date.parse('2026-10-14T22:55:00+09:00'));
    assert.equal(r.arrival, Date.parse('2026-10-14T23:55:00+09:00'));
});
test('valid leap day and year boundary are preserved', () => {
    assert.equal(schedule.parseAppointment('2028-02-29T10:00'), Date.parse('2028-02-29T10:00:00+09:00'));
    const r = schedule.calculate('2027-01-01T00:10', 1200, 20, now);
    assert.equal(r.departure, Date.parse('2026-12-31T23:30:00+09:00'));
});
test('invalid dates are rejected without Date auto-correction', () => {
    for (const value of ['', '2026-02-29T10:00', '2026-04-31T10:00', '2026-13-01T10:00', '2026-10-14T24:00', 'bad']) {
        assert.equal(schedule.parseAppointment(value), null, value);
    }
});
test('past or current appointments are rejected', () => {
    assert.equal(schedule.calculate('2026-10-14T09:00', 3000, 10, now).status, 'PAST_APPOINTMENT');
    assert.equal(schedule.calculate('2026-10-13T19:00', 3000, 10, now).status, 'PAST_APPOINTMENT');
});
test('passed departure retains useful recommended times and warns', () => {
    const r = schedule.calculate('2026-10-14T09:30', 3000, 10, now);
    assert.equal(r.status, 'DEPARTURE_PASSED');
    assert.equal(r.departure, Date.parse('2026-10-14T08:30:00+09:00'));
});
test('missing, negative, or non-finite durations cannot create a schedule', () => {
    for (const duration of [null, undefined, -1, NaN, Infinity, '3000']) {
        assert.equal(schedule.calculate('2026-10-14T19:00', duration, 10, now).status, 'NO_DURATION');
    }
});
test('zero duration and zero buffer are valid; seconds round up conservatively', () => {
    assert.equal(schedule.calculate('2026-10-14T19:00', 0, 0, now).departure, Date.parse('2026-10-14T19:00:00+09:00'));
    assert.equal(schedule.calculate('2026-10-14T19:00', 61, 0, now).departure, Date.parse('2026-10-14T18:58:00+09:00'));
    assert.equal(schedule.calculate('2026-10-14T19:00', 3000, -10, now).status, 'INVALID_BUFFER');
});
test('formatting always uses Korea time with date', () => {
    const value = schedule.formatTime(Date.parse('2026-10-14T18:00:00+09:00'));
    assert.match(value, /10.*14/);
    assert.match(value, /18:00/);
});
test('minimum input is the next minute in Korean time', () => {
    assert.equal(schedule.minimumAppointment(now + 12000), '2026-10-14T09:01');
    assert.equal(schedule.minimumAppointment(now), '2026-10-14T09:01');
});
