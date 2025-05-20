# migration

A Clojure library designed to run database migrations from a list of SQL files.

## Usage

```
(run-migrations
    <jdbc-connection>
    :current-revision-fn  <function to get current revision number>
    :update-revision-fn   <function to update the current revision number>
    :init-fn              <function to initialize the database>
    :target-revision      <the revision to migrate to from the current-revision>)
```

## License

Copyright © 2025 Patrick Roche

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
