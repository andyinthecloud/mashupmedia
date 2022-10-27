import {useEffect, useState } from 'react'
import React from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../common/redux/store';
import { getImage } from './rest/mediaCalls';
import { blob } from 'stream/consumers';


type SecureImagePayload = {
    path: string
    objectUrl?: string
}

const SecureImage = (payload: SecureImagePayload) => {



    const [props, setProps] = useState<SecureImagePayload>()


    useEffect(() => {
        setProps(payload)
    }), [payload]

    const imageRef = React.useRef<HTMLImageElement>(null)

    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    useEffect(() => {

        if (props?.path == null) {
            return
        } 


        getImage(props.path, userToken).then(response => response.blob())
        .then(blob => {
            // if (response.blob == null) {
            //     return null
            // }

            const objectUrl = URL.createObjectURL(new Blob([blob]))

            if (objectUrl) {
                setProps(p => ({
                    path: p?.path ? p.path : '',
                    objectUrl: objectUrl
                }))
            }
        
        })

    }), [userToken]

    return (
        <img 
            src={props?.objectUrl}
        />
    )
}

export default SecureImage