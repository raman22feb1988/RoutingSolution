/*
 * Copyright (c) 2011-2019 HERE Global B.V. and its affiliate(s).
 * All rights reserved.
 * The use of this software is conditional upon having a separate agreement
 * with a HERE company for the use or utilization of this software. In the
 * absence of such agreement, the use of the software is not allowed.
 */

package com.example.hereapi.locationservices;

import com.here.android.mpa.search.AutoSuggest;

import java.util.List;

public interface AsyncResponse2 {
    void processFinish1(String output1, String output2);
    void processFinish2(List<AutoSuggest> output1, String output2);
}