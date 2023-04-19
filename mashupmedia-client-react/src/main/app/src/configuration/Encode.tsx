import { Button, TextField } from "@mui/material"
import { useSelector } from "react-redux"
import { RootState } from "../common/redux/store"
import { NameValuePayload } from "./backend/metaCalls"
import { useEffect, useState } from "react";
import { getFfmpegInstallation, saveFfmpegInstallation, verifyFfmpegInstallation } from "./backend/encodeCalls";
import { useDispatch } from "react-redux";
import { NotificationType, addNotification } from "../common/notification/notificationSlice";
import { useNavigate } from "react-router-dom";


type EncodingPayload = {
    ffmpegPayload: NameValuePayload<string>
}


export const Encode = () => {

    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)
    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const [props, setProps] = useState<EncodingPayload>({
        ffmpegPayload: {
            name: "path",
            value: ""
        }
    })

    useEffect(() => {
        getFfmpegInstallation(userToken)
            .then(response => {
                const ffmpegPayload = response.parsedBody
                    ? response.parsedBody
                    : null

                if (!ffmpegPayload) {
                    return
                }

                setProps(p => ({
                    ...p,
                    ffmpegPayload
                }))
            })

    }, [userToken])

    const dispatch = useDispatch()

    const handleTest = (): void => {

        verifyFfmpegInstallation(props.ffmpegPayload.value, userToken)
            .then(response => {
                console.log("verifyFfmpegInstallation", response)
                if (response.ok) {

                    if (response.parsedBody) {
                        dispatch(
                            addNotification({
                                message: 'Ffmpeg installation path verified.',
                                notificationType: NotificationType.SUCCESS
                            })
                        )
                    } else {
                        dispatch(
                            addNotification({
                                message: 'Ffmpeg installation cannot be found.',
                                notificationType: NotificationType.ERROR
                            })
                        )
                    }
                }

            })

        console.log("handle test")
    }

    const navigate = useNavigate()

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>): void => {
        e.preventDefault()
        saveFfmpegInstallation(props.ffmpegPayload, userToken)
            .then(response => {
                if (response.ok) {
                    dispatch(
                        addNotification({
                            message: 'Encoding path saved.',
                            notificationType: NotificationType.SUCCESS
                        })
                    )
                    navigate('/configuration/encode')

                }
            })


    }

    const setPathValue = (path: string): void => {
        setProps(p => ({
            ...p,
            ffmpegPayload: {
                ...p.ffmpegPayload,
                value: path
            }
        }))
    }

    return (
        <form onSubmit={handleSubmit}>
            <h1>Encode configuration</h1>

            <div className="new-line">
                <TextField
                    name="path"
                    value={props?.ffmpegPayload.value}
                    label="Path to the FFmpeg executable"
                    onChange={e => setPathValue(e.currentTarget.value)}
                    helperText="The ffmpeg executable file is normally located in the ffmpeg &quot;bin&quot; folder."
                />
            </div>

            <div className="new-line right">

                {userPolicyPayload?.administrator &&
                    <Button variant="contained" color="secondary" type="button" onClick={handleTest}>
                        Test
                    </Button>
                }

                {userPolicyPayload?.administrator &&
                    <Button variant="contained" color="primary" type="submit">
                        Save
                    </Button>
                }

            </div>

        </form>

    )
}

export default Encode