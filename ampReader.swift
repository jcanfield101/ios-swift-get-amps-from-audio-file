 func getAmps(path:String) -> [Int16] {
        var url = NSURL(fileURLWithPath: path)
        var avasset = AVAsset.assetWithURL(url!) as AVAsset
        var samplez = [Int16]()
        var reader:AVAssetReader? = AVAssetReader(asset: avasset, error: &err)
        var songTrack = avasset.tracks[0] as AVAssetTrack
        var output:AVAssetReaderTrackOutput? = AVAssetReaderTrackOutput(track: songTrack, outputSettings: outputSettingsDict)
        reader?.addOutput(output)
        var sampleRate = UInt32()
        var channelCount = UInt32()
        reader?.startReading()
        var totalBytes = Int(0);
        var currentSample : Int = 1
        var maxx = Int16(0)
        var peak = Int16(0)
        var sampleBufferRef:CMSampleBufferRef!

        while (reader?.status == AVAssetReaderStatus.Reading) {
            let trackOutput = reader?.outputs[0] as AVAssetReaderTrackOutput?
            sampleBufferRef = trackOutput?.copyNextSampleBuffer()

            if sampleBufferRef != nil {
                let blockBufferRef = CMSampleBufferGetDataBuffer(sampleBufferRef)
                let length:size_t = CMBlockBufferGetDataLength(blockBufferRef)
                totalBytes += Int(length)
                var data = NSMutableData(length: Int(length))
                CMBlockBufferCopyDataBytes(blockBufferRef, UInt(0), UInt(length), data!.mutableBytes)
                var sample = UnsafePointer<Int16>(data!.mutableBytes)
                let sampleCount = Int(length) / 4 // bytes per sample = 4

                for var i = 0; i < sampleCount; i++ {
                    let val = Float(sample.memory)
                    var val2 = Int16(min(32767.0, abs(val)))
                    sample++
                    sample++

                    if val2 > maxx { maxx = val2 }

                    if currentSample == advanceBy-1 {
                        peak = Int16(Float(peak) * 0.7)

                        if peak > maxx { maxx = peak} else {peak = maxx}
                        samplez.append(maxx)
                        maxx = 0
                        currentSample = 0
                    }
                    currentSample++
                }
                CMSampleBufferInvalidate(sampleBufferRef);
            }
        }
        return samplez
    }