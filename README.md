# utility-algorithms-java

Multi purpose utility algorithms.
The algorithms and classes are all each cool (duh),
   but each not big enough for their own repository.

Some of the algorithms here may be implementation of protocols that exist in other "utility-algorithms-*lang*" repos.





### Installing

* Clone the repository with the 'git clone' command for now. (may turn this into a maven repo at some point)
* Or copy just copy the packages you might need, just don't forget the license or mail me if you cannot include it

## Features

 * ubae - universal byte array encoder (also: *rust*) - NoSQL database system using 'tags' as data identifier. Allows storing any kind of data. Works in memory, on disk, remote and remote with multiple users. (downside: Search is currently in O(n))
 * use - universal string encoder (also: *python*, *go*) - Multiple sets of utf8 data? Only one String to store it all? Use this. (or json, but who would do that?)
 * mcnp - multi chunk network protocol (also: *go*, *rust*) - protocol for simple, low level cross programming language network communication. Uses TCP-Sockets. (downside: currently lacks async and timeout functionality, consider using P2Link instead)
 * bitsandbytes - certain helper functionality concerning lower level operations
 * date_time - (yeah yeah never build a date api yourself) - a very simplistic date and time api for when dealing with timezones and complex stuff is overkill
 * timediffmarker(also: *rust*) and callcounter - fairly dumb debugging tool. Only slightly better than Sys.out's
 * asap_queue - fairly dumb 'defer call until possible' queue (uses exceptions for flow control, so use with a strong stomach only)
 * vsrb - variable sized element ring buffer - capacitated ring buffer with queue and stack operations on arbitrary storage(in RAM, on File, remotely)

## Usage

See the java doc commentary on the api's themselves

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details