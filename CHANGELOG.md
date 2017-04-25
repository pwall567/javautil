# Change Log
Added this change log after project was already under way.  Early changes are not noted.

The format is based on [Keep a Changelog](http://keepachangelog.com/).

## [Unreleased]
### Changed
- Multiple classes modified for Java 8
- Added JavaDoc to several classes

### Removed
- `AbstractCharMapper` (functionality moved to `CharMapper` interface)

## [1.3]
### Changed
- Added JavaDoc to `ListArray`

### Added
- New class `ListMap`
- New class `SortedListMap`

## [1.2] - 2017-02-19
### Added
- New class `ReaderBuffer`

## [1.1] - 2016-11-27
### Changed
- Modified `ISO8601Date` to improve handling of time zones, and to use new `Strings` methods

### Added
- Rudimentary unit test for `ISO8601Date`
- New method `Strings.appendInt()` (optimised output of int values)
- Also `Strings.appendLong()`, `Strings.append2Digits`, `Strings.append3Digits`

## [1.0.2] - 2015-12-31
### Changed
- Added `plural` to `Strings`
- More enhancements to `SyncQueue`

## [1.0.1] - 2015-12-30
### Changed
- Added functionality to `SyncQueue`
- Minor fixes to `ParseText`

## [1.0] - 2015-12-11
### Added
- Initial release
